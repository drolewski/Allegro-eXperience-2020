# Summer e-Xperience 2020
Software Engineer (CRM) - Intern

# Struktura danych
Dane pobierane z przykładowej tabeli `allegro_clients`, która tworzona jest przez wywołanie
komend SQL przedstawionych w pliku [allegroClient.sql](https://github.com/drolewski/Allegro-eXperience-2020/blob/master/src/main/resources/static/allegroClient.sql). Struktura ta nie posiada hierarchii
i podziału na klientów firmowych i indywidualnych. Dane z tej struktury są pobierane
w całości jako surowe nieobrobione recordy tabeli.
Następnie zapisywane są do tabeli `allegro_client_deduplicated` utworzonej na podstawie
skryptu [deduplicatedClients.sql](https://github.com/drolewski/Allegro-eXperience-2020/blob/master/src/main/resources/static/deduplicatedClients.sql). Dane w tej tabeli przechowywane są w sposób hierarchiczny.
Istnieją dwa klucze obce, które przechowują identyfikator swojego rodzica, czyli np.
pierwszy rekord firmy z danym numerem NIP. 
Dzięki takiej strukturyzacji w łatwy sposób jestem w stanie pobierać informacje na
temat podległych danej firmie kont. A dzięki temu, że dane znajdują sie w jednej tabeli
oszczędzam przepływ danych między plikami bazy danych do minimum. Nie następują nadmiarowe
odczyty i zapisy między tabelami bazy danych, co znacząco wpływa na optymalizację 
po stronie bazy danych.

# Implementacja
Cała usługa wykonana jest w postaci RESTowego API. Dostępne zapytania:

    [GET] /allegro/ -> Wszystkie rekordy z tabeli zdeduplikowanych klientów
    
    [GET] /allegro/nip/{nip} -> Rekordy klientów o podanym numerze nip
    
    [GET] /allegro/name/{name} -> Rekordy klientów o podanym imieniu i nazwisku
    
    [GET] /allegro/import -> Przeprowadza proces deduplikacji, importu oraz zwraca wszystkie dane z tabeli
    
Proces deduplikacji polega na znalezieniu rekordów z powtarzającymi się loginami, 
numerami nip, emailami (oraz przetworzeniem ich na historyczne), a także hierarchizacją
danych w tabeli wynikowej

# Autor
[Dominik Rolewski](https://github.com/drolewski)