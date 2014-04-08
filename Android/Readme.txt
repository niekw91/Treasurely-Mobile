Gebruik van Treasurely iOS
--------------------------
1. Log in met een van de volgende gegevens:

Gebruikersnaam				Wachtwoord
---------------------------------------------------			
test@test.com				t3st
niek@test.com				12345
este@test.com				12345


2. De applicatie zal gebruik maken van locatie bepaling er zijn 2 manier om dit te doen:

   DDMS > Emulator Control > Location Controls. 
   Voer vervolgens de de volgende coordinaten in:
	latitude: 	37,422006
	longitude:	-122,084095
   De simulator heeft nu de coordinaten als locatie ingesteld, i.v.m. de
   vele problemen die we tegenkwamen ivm onbetrouwbaarheid is het niet aan te raden deze te gebruiken.

   Hardcoded
   De app krijgt als de coordinaten 0.0 zijn automatisch
	latitude: 	37,422006
	longitude:	-122,084095
   
   Zodra het icoontje rechtboven zichtbaar is,
   staat de locatiebepaling aan (dit kan soms even duren)

3. Via drop kan er een treasure worden achtergelaten op de huidige locatie, bij het 
   zoeken naar treasures zullen degene in een straal van 500 meter worden getoond