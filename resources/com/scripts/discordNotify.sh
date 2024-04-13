#!/bin/bash

# Definir a URL do webhook
WEBHOOK_URL="https://discord.com/api/webhooks/1228683845180592128/nR4a8eMDLtrhym8IMu_58huf5YxZf0N6LNLUyC4boSXHa-cc3FB4SafHICDIkPjX31M4"
DATA_HORA=$(date +"%d-%m-%Y %H-%M-%S")

send_build_alert() {
    if [ $# -lt 6 ]; then
        echo "Uso: $0 send_build_alert PROJETO BRANCH BUILD COMMIT AUTOR STATUS"
        exit 1
    fi

    PROJECT=$1
    BRANCH=$2
    BUILD=$3
    COMMIT=$4
    AUTHOR=$5
    BUILD_URL=$6

    MESSAGE="--------------------------------------------------------------- \n\
⚠️ **ALERTA DE BUILD** ⚠️ \n\
--------------------------------------------------------------- \n\
- **Projeto:** $PROJECT \n\
- **Branch:** $BRANCH \n\
- **Build:** $BUILD \n\
- **Commit:** $COMMIT \n\
- **Autor:** $AUTHOR \n\
- **Data e Hora:** $DATA_HORA \n\
- **URL:** $BUILD_URL \n\
- **Status:** Building... ⚠️ \n\
---------------------------------------------------------------" 

    DATA="{\"content\":\"$MESSAGE\"}"
    curl -X POST -H "Content-Type: application/json" -d "$DATA" "$WEBHOOK_URL"
}

send_success_alert() {
    if [ $# -lt 6 ]; then
        echo "Uso: $0 send_build_alert PROJETO BRANCH BUILD COMMIT AUTOR STATUS"
        exit 1
    fi

    PROJECT=$1
    BRANCH=$2
    BUILD=$3
    COMMIT=$4
    AUTHOR=$5
    BUILD_URL=$6

    MESSAGE="--------------------------------------------------------------- \n\
✅ **BUILD SUCCESS** ✅ \n\
--------------------------------------------------------------- \n\
- **Projeto:** $PROJECT \n\
- **Branch:** $BRANCH \n\
- **Build:** $BUILD \n\
- **Commit:** $COMMIT \n\
- **Autor:** $AUTHOR \n\
- **Data e Hora:** $DATA_HORA \n\
- **URL:** $BUILD_URL \n\
- **Status:** Success... ✅ \n\
---------------------------------------------------------------" 

    DATA="{\"content\":\"$MESSAGE\"}"
    curl -X POST -H "Content-Type: application/json" -d "$DATA" "$WEBHOOK_URL"
}

send_faliure_alert() {
    if [ $# -lt 6 ]; then
        echo "Uso: $0 send_build_alert PROJETO BRANCH BUILD COMMIT AUTOR STATUS"
        exit 1
    fi

    PROJECT=$1
    BRANCH=$2
    BUILD=$3
    COMMIT=$4
    AUTHOR=$5
    BUILD_URL=$6

    MESSAGE="--------------------------------------------------------------- \n\
❌ **BUILD FALIURE** ❌ \n\
--------------------------------------------------------------- \n\
- **Projeto:** $PROJECT \n\
- **Branch:** $BRANCH \n\
- **Build:** $BUILD \n\
- **Commit:** $COMMIT \n\
- **Autor:** $AUTHOR \n\
- **Data e Hora:** $DATA_HORA \n\
- **URL:** $BUILD_URL \n\
- **Status:** Faliure... ❌ \n\
---------------------------------------------------------------" 

    DATA="{\"content\":\"$MESSAGE\"}"
    curl -X POST -H "Content-Type: application/json" -d "$DATA" "$WEBHOOK_URL"
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
  send_faliure_alert)
    shift
    send_faliure_alert "$@"
    ;;
  *)
    echo "Uso: $0 {send_build_alert|send_success_alert|send_faliure_alert} ..."
    exit 1
    ;;
esac