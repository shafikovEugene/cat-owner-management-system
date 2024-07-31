#!/bin/bash

MIGRATIONS_DIR="migrations"

if [ -z $DB_VERSION ]; then
  DB_VERSION=$(basename $(ls -v "/postgres-init/$MIGRATIONS_DIR"/*.sql | tail -n 1) .sql)
  echo "!DB_VERSION not found, migration to the last version: $DB_VERSION!"
else
  echo "!Migrating to the version: $DB_VERSION!"
fi

for MIGRATION_FILE in $(ls -v "/postgres-init/$MIGRATIONS_DIR"/*.sql); do
    CURRENT_VERSION=$(basename $MIGRATION_FILE .sql)
    echo "Migrating version: $CURRENT_VERSION"
    psql -U $POSTGRES_USER -d $POSTGRES_DB -f $MIGRATION_FILE
    
    if [ "$CURRENT_VERSION.sql" = "$DB_VERSION.sql" ]; then
      echo "!Migration complete!"
      break
    fi
done