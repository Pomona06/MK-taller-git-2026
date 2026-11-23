#!/usr/bin/env bash
# POO-09: guarda las respuestas reales del escenario con los tres servicios prendidos,
# para que la corrección las vea aunque un compañero esté apagado ese día.
#
# Uso (desde la carpeta del proyecto, con la base de este servicio vacía):
#   ./insomnia/capturar-respuestas.sh
# Las URL se pueden cambiar con A1_URL, A2_URL y A3_URL.
set -u

A1="${A1_URL:-http://localhost:8082}"   # este servicio (Marcelo)
A2="${A2_URL:-http://localhost:8080}"   # Matías: disparo rápido
A3="${A3_URL:-http://localhost:8083}"   # Joaquín: daño con blindaje
DIR="$(cd "$(dirname "$0")" && pwd)/respuestas"
mkdir -p "$DIR"
rm -f "$DIR"/[0-9][0-9]-*.json   # las de una captura anterior
RESUMEN="$DIR/resumen.md"

{
  echo "# Respuestas reales del escenario de POO-09"
  echo
  echo "Capturadas el $(date '+%Y-%m-%d %H:%M') con \`insomnia/capturar-respuestas.sh\`."
  echo
  echo "- A1 (Marcelo): $A1, commit \`$(git rev-parse --short HEAD 2>/dev/null || echo '?')\`"
  echo "- A2 (Matías): $A2"
  echo "- A3 (Joaquín): $A3"
  echo
  echo "| # | Pedido | HTTP |"
  echo "|---|---|---|"
} > "$RESUMEN"

ULTIMA=""
# pedir NUMERO NOMBRE METODO URL [CUERPO]: guarda {"status": ..., "respuesta": ...} en respuestas/NUMERO-NOMBRE.json
pedir() {
  local num="$1" nombre="$2" metodo="$3" url="$4" cuerpo="${5:-}"
  local salida
  if [ -n "$cuerpo" ]; then
    salida=$(curl -s -X "$metodo" "$url" -H 'Content-Type: application/json' -d "$cuerpo" -w $'\n%{http_code}')
  else
    salida=$(curl -s -X "$metodo" "$url" -w $'\n%{http_code}')
  fi
  local codigo="${salida##*$'\n'}"
  ULTIMA="${salida%$'\n'*}"
  [ -z "$ULTIMA" ] && ULTIMA="null"
  printf '{"pedido": "%s %s", "status": %s, "respuesta": %s}\n' "$metodo" "$url" "$codigo" "$ULTIMA" > "$DIR/$num-$nombre.json"
  echo "| $num | \`$metodo $url\` | $codigo |" >> "$RESUMEN"
  echo "$num $nombre -> HTTP $codigo"
}
id_de_la_ultima() { echo "$ULTIMA" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2; }

pedir 01 openapi-a1 GET "$A1/v3/api-docs"
pedir 02 openapi-a2 GET "$A2/v3/api-docs"
pedir 03 openapi-a3 GET "$A3/v3/api-docs"
pedir 04 crear-awp POST "$A1/api/armas" '{"modelo":"AWP","equipo":"CT"}';                 AWP=$(id_de_la_ultima)
pedir 05 crear-halcon POST "$A1/api/jugadores" '{"nombre":"Halcón","equipo":"TT","chaleco":true}'; HALCON=$(id_de_la_ultima)
pedir 06 crear-aliado POST "$A1/api/jugadores" '{"nombre":"Aliado","equipo":"CT"}';         ALIADO=$(id_de_la_ultima)
pedir 07 crear-sombra POST "$A1/api/jugadores" '{"nombre":"Sombra","equipo":"CT"}';         SOMBRA=$(id_de_la_ultima)
pedir 08 escenario-feliz POST "$A1/api/escenarios/disparos-rapidos" "{\"armaId\":$AWP,\"tiradorId\":$SOMBRA,\"objetivoId\":$HALCON,\"distancia\":40,\"conMira\":true}"
pedir 09 jugadas GET "$A1/api/escenarios/disparos-rapidos"
pedir 10 awp-despues GET "$A1/api/armas/$AWP"
pedir 11 halcon-despues GET "$A1/api/jugadores/$HALCON"
pedir 12 rechazo-a2-sin-mira POST "$A1/api/escenarios/disparos-rapidos" "{\"armaId\":$AWP,\"tiradorId\":$SOMBRA,\"objetivoId\":$HALCON,\"distancia\":40,\"conMira\":false}"
pedir 13 rechazo-a3-fuego-amigo POST "$A1/api/escenarios/disparos-rapidos" "{\"armaId\":$AWP,\"tiradorId\":$SOMBRA,\"objetivoId\":$ALIADO,\"distancia\":40,\"conMira\":true}"
pedir 14 rechazo-propio-tirador-de-otro-equipo POST "$A1/api/escenarios/disparos-rapidos" "{\"armaId\":$AWP,\"tiradorId\":$HALCON,\"objetivoId\":$ALIADO,\"distancia\":40,\"conMira\":true}"
pedir 15 rechazo-propio-fuera-de-alcance POST "$A1/api/escenarios/disparos-rapidos" "{\"armaId\":$AWP,\"tiradorId\":$SOMBRA,\"objetivoId\":$HALCON,\"distancia\":300,\"conMira\":true}"
pedir 16 jugadas-despues-de-los-rechazos GET "$A1/api/escenarios/disparos-rapidos"
pedir 17 awp-despues-de-los-rechazos GET "$A1/api/armas/$AWP"

echo
echo "Listo: respuestas en $DIR (resumen en $RESUMEN)."
