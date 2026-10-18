#!/usr/bin/env bash
# Recorre el flujo completo a través del API Gateway, con la plataforma levantada
# (docker compose up -d). Uso: ./scripts/prueba-e2e.sh [https://localhost:8443]
set -euo pipefail
BASE="${1:-https://localhost:8443}/api/v1"
FALLAS=0

# -k: el gateway usa un certificado autofirmado en el entorno académico.
CUERPO_RESP=$(mktemp); trap 'rm -f "$CUERPO_RESP"' EXIT
llamar() { curl -sk -o "$CUERPO_RESP" -w "%{http_code}" "$@"; }
campo() { python3 -c "import json,sys; print(json.load(open(sys.argv[1]))$1)" "$CUERPO_RESP"; }
esperar() {
  local nombre="$1" esperado="$2" obtenido="$3"
  if [[ "$obtenido" == "$esperado" ]]; then echo "  ok   $nombre ($obtenido)"; else
    echo "  FALLA $nombre: se esperaba $esperado y llegó $obtenido"; cat "$CUERPO_RESP"; echo; FALLAS=$((FALLAS+1)); fi
}

echo "Seguridad"
esperar "sin token → 401" 401 "$(llamar "$BASE/habitaciones")"
esperar "clave incorrecta → 401" 401 "$(llamar -X POST "$BASE/auth/token" -H 'Content-Type: application/json' -d '{"usuario":"cliente","clave":"x"}')"
esperar "token de cliente" 200 "$(llamar -X POST "$BASE/auth/token" -H 'Content-Type: application/json' -d '{"usuario":"cliente","clave":"cliente123"}')"
CLI="Authorization: Bearer $(campo "['access_token']")"
esperar "token de recepción" 200 "$(llamar -X POST "$BASE/auth/token" -H 'Content-Type: application/json' -d '{"usuario":"recepcion","clave":"recepcion123"}')"
ADM="Authorization: Bearer $(campo "['access_token']")"
esperar "cliente no crea paquetes → 403" 403 "$(llamar -X POST "$BASE/paquetes" -H "$CLI" -H 'Content-Type: application/json' -d '{"nombre":"x","precio":1}')"

echo "Clientes"
SUF=$RANDOM$RANDOM
esperar "registrar" 201 "$(llamar -X POST "$BASE/clientes" -H "$CLI" -H 'Content-Type: application/json' -d "{\"nombres\":\"Luis\",\"apellidos\":\"Tabaco Perez\",\"email\":\"luis$SUF@example.com\",\"documento\":{\"tipo\":\"DNI\",\"numero\":\"7$SUF\"},\"telefono\":\"+51 988777666\"}")"
CLIENTE=$(campo "['id']")
esperar "email duplicado → 409" 409 "$(llamar -X POST "$BASE/clientes" -H "$CLI" -H 'Content-Type: application/json' -d "{\"nombres\":\"Otro\",\"apellidos\":\"X\",\"email\":\"luis$SUF@example.com\",\"documento\":{\"tipo\":\"DNI\",\"numero\":\"8$SUF\"}}")"
esperar "datos inválidos → 400" 400 "$(llamar -X POST "$BASE/clientes" -H "$CLI" -H 'Content-Type: application/json' -d '{"nombres":"","email":"no-es-correo"}')"
esperar "obtener $CLIENTE" 200 "$(llamar "$BASE/clientes/$CLIENTE" -H "$CLI")"
esperar "inexistente → 404" 404 "$(llamar "$BASE/clientes/CLI-999999" -H "$CLI")"

echo "Catálogo"
esperar "disponibilidad de suites" 200 "$(llamar "$BASE/habitaciones?desde=2026-11-01&hasta=2026-11-04&tipo=SUITE" -H "$CLI")"
esperar "rango inválido → 400" 400 "$(llamar "$BASE/habitaciones?desde=2026-11-04&hasta=2026-11-01" -H "$CLI")"
esperar "tours del 2-nov" 200 "$(llamar "$BASE/tours?fecha=2026-11-02" -H "$CLI")"
esperar "paquetes activos" 200 "$(llamar "$BASE/paquetes?activo=true" -H "$CLI")"

# Cada corrida usa un mes distinto para no chocar con las reservas de la corrida anterior.
MES=$(( (SUF % 9) + 1 )); D1=$(printf "2027-%02d-10" $MES); D2=$(printf "2027-%02d-13" $MES)
esperar "crear tour para la estadía" 201 "$(llamar -X POST "$BASE/tours" -H "$ADM" -H 'Content-Type: application/json' -d "{\"nombre\":\"Tour de prueba\",\"fecha\":\"$(printf "2027-%02d-11" $MES)\",\"cupo_total\":5,\"precio\":380}")"
TOUR=$(campo "['id']")

echo "Reserva integrada (habitación + tour + pago)"
CLAVE="e2e-$SUF"
CUERPO="{\"cliente_id\":\"$CLIENTE\",\"habitacion_id\":\"HAB-301\",\"desde\":\"$D1\",\"hasta\":\"$D2\",\"tour_id\":\"$TOUR\",\"paquete_id\":null,\"pago\":{\"metodo\":\"TARJETA\",\"token_tarjeta\":\"tok_abc123\"}}"
SIN_TOUR="${CUERPO/\"$TOUR\"/null}"
esperar "sin Idempotency-Key → 400" 400 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H 'Content-Type: application/json' -d "$CUERPO")"
esperar "crear" 201 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE" -H 'Content-Type: application/json' -d "$CUERPO")"
RESERVA=$(campo "['reserva_id']"); TOTAL=$(campo "['total']")
esperar "total 920 x 3 + 380" 3140.0 "$TOTAL"
esperar "reintento con la misma clave → misma reserva" 201 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE" -H 'Content-Type: application/json' -d "$CUERPO")"
esperar "  … con el mismo id" "$RESERVA" "$(campo "['reserva_id']")"
esperar "misma clave, otro cuerpo → 409" 409 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE" -H 'Content-Type: application/json' -d "${CUERPO/HAB-301/HAB-305}")"
esperar "misma habitación y fechas → 409 sin cupo" 409 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE-b" -H 'Content-Type: application/json' -d "${SIN_TOUR}")"
esperar "  … con sugerencias" True "$(campo ".get('sugerencias', []) != []")"
esperar "pago rechazado → 402" 402 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE-c" -H 'Content-Type: application/json' -d "$(echo "$CUERPO" | sed 's/HAB-301/HAB-305/; s/tok_abc123/tok_rechazo/')")"
esperar "  … y la HAB-305 quedó libre" 201 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE-d" -H 'Content-Type: application/json' -d "$(echo "$CUERPO" | sed 's/HAB-301/HAB-305/; s/"tour_id":"[^"]*"/"tour_id":null/')")"
esperar "consultar $RESERVA" 200 "$(llamar "$BASE/reservas/$RESERVA" -H "$CLI")"

echo "Cancelación"
esperar "cancelar" 200 "$(llamar -X DELETE "$BASE/reservas/$RESERVA" -H "$CLI")"
esperar "  … pago reembolsado" REEMBOLSADO "$(campo "['pago']['estado']")"
esperar "cancelar otra vez → 409" 409 "$(llamar -X DELETE "$BASE/reservas/$RESERVA" -H "$CLI")"
esperar "la HAB-301 vuelve a estar libre" 201 "$(llamar -X POST "$BASE/reservas" -H "$CLI" -H "Idempotency-Key: $CLAVE-e" -H 'Content-Type: application/json' -d "${SIN_TOUR}")"

echo
if [[ $FALLAS -eq 0 ]]; then echo "Todo en verde."; else echo "$FALLAS falla(s)."; exit 1; fi
