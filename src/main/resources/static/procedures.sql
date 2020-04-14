delimiter //
DROP PROCEDURE IF EXISTS importData;
CREATE PROCEDURE importData()
BEGIN
	SELECT distinct *
    FROM allegro.allegro_client;
END;

DROP PROCEDURE IF EXISTS getDeduplicated()
CREATE PROCEDURE getDeduplicated(
	IN pNIP VARCHAR(45)
)
BEGIN
	SELECT *
    FROM allegro.allegro_client_deduplicated WHERE REPLACE(nip, '-', '') LIKE pNIP;
END;