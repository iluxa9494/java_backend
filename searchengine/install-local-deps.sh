#!/bin/bash

set -e

echo "🔧 Установка всех Morphology JARs в локальный Maven-репозиторий..."

declare -A jars=(
  ["russian"]="russian-1.5.jar"
  ["english"]="english-1.5.jar"
  ["lucene-core"]="lucene-core-8.11.0.jar"
  ["lucene-analyzers-common"]="lucene-analyzers-common-8.11.0.jar"
  ["morph"]="morph-1.5.jar"
  ["morphology"]="morphology-1.5.jar"
  ["dictionary-reader"]="dictionary-reader-1.5.jar"
)

for artifact in "${!jars[@]}"; do
  file="apache_lucene_morphology/${jars[$artifact]}"
  if [ -f "$file" ]; then
    echo "📦 Установка $artifact из $file"
    mvn install:install-file \
      -Dfile="$file" \
      -DgroupId=org.apache.lucene.morphology \
      -DartifactId="$artifact" \
      -Dversion=1.5 \
      -Dpackaging=jar \
      -DgeneratePom=true \
      -DlocalRepositoryPath=local-repo
  else
    echo "❌ Файл $file не найден, пропуск..."
  fi
done

echo "✅ Установка завершена!"
