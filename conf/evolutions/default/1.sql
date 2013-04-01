# Documents schema

# --- !Ups
CREATE TABLE "station" (
    "id" integer NOT NULL,
    "name" text NOT NULL,
    "watercourse" text NOT NULL
);

CREATE TABLE "measurement" (
    "guid" char(36) NOT NULL,
    "stationId" integer NOT NULL,
    "takenAt" datetime NOT NULL,
    "level" double NOT NULL,
    "typicalLow" double NOT NULL,
    "typicalHigh" double NOT NULL,
    FOREIGN KEY ("stationId") REFERENCES "station"("id")
);

CREATE UNIQUE INDEX measurement_details_idx ON "measurement" ("stationId", "takenAt");

CREATE TABLE "harvesttarget" (
    "guid" char(36) NOT NULL,
    "stationId" integer NOT NULL
);

CREATE UNIQUE INDEX harvesttargets_idx ON "harvesttarget" ("stationId");

# --- !Downs
DROP INDEX harvesttargets_idx ON "harvesttarget";
DROP TABLE "harvesttarget";

DROP INDEX measurement_details_idx ON "measurement";
DROP TABLE "measurement";

DROP TABLE "station";
