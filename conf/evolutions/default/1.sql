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

CREATE TABLE "harvesttargets" (
    "guid" char(36) NOT NULL,
    "stationId" integer NOT NULL
);

# --- !Downs

DROP TABLE "harvesttargets";

DROP INDEX "measurement_details_idx";
DROP TABLE "measurement";

DROP TABLE "station";
