#!/bin/bash

# Defina suas variáveis
TOKEN="7027603350:AAGt2HsCMrRYp5BMa44kKUluC0dE_tha_xc"
CHAT_ID="-4178298213"

send_build_alert() {
  if [ $# -lt 5 ]; then
    echo "Uso: $0 send_build_alert PROJETO BRANCH BUILD COMMIT AUTOR"
    exit 1
  fi

  PROJECT=$1
  BRANCH=$2
  BUILD=$3
  COMMIT=$4
  AUTHOR=$5

  MESSAGE="
  ⚠️ ALERTA ⚠️
  - <b>Projeto:</b> $PROJECT
  - <b>Branch:</b> $BRANCH
  - <b>Build:</b> $BUILD
  - <b>Commit:</b> $COMMIT
  - <b>Autor:</b> $AUTHOR
  - <b>Status:</b> Building... ⚠️
  "

  curl -s -X POST \
    https://api.telegram.org/bot$TOKEN/sendMessage \
    -d chat_id=$CHAT_ID \
    -d text="$MESSAGE" \
    -d parse_mode="HTML"
}

send_success_alert() {
  if [ $# -lt 5 ]; then
    echo "Uso: $0 send_build_alert PROJETO BRANCH BUILD COMMIT AUTOR"
    exit 1
  fi

  PROJECT=$1
  BRANCH=$2
  BUILD=$3
  COMMIT=$4
  AUTHOR=$5

  MESSAGE="
  ✅ SUCESSO ✅
  - <b>Projeto:</b> $PROJECT
  - <b>Branch:</b> $BRANCH
  - <b>Build:</b> $BUILD
  - <b>Commit:</b> $COMMIT
  - <b>Autor:</b> $AUTHOR
  - <b>Status:</b> Concluído com sucesso! ✅ 
  "

  curl -s -X POST \
    https://api.telegram.org/bot$TOKEN/sendMessage \
    -d chat_id=$CHAT_ID \
    -d text="$MESSAGE" \
    -d parse_mode="HTML"
}

case $1 in
  send_build_alert)
    shift
    send_build_alert "$@"
    ;;
  send_success_alert)
    shift
    send_success_alert "$@"
    ;;
  *)
    echo "Uso: $0 {send_build_alert|send_success_alert} ..."
    exit 1
    ;;
esac

# ./envio-msg.sh send_build_alert "Meu Projeto" "Minha Branch" "123" "abc123" "João" "Em progresso"
# ./envio-msg.sh send_success_alert "Meu Projeto" "123" "Concluído com sucesso"


