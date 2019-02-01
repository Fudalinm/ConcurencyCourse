Zadanie - producenci i konsumenci z losowa iloscia pobieranych i wstawianych porcji
Bufor może pomieścić 2M nierozróżnialnych elementów
Jest wielu producentów i konsumentów
Producent wstawia do bufora losową liczbę elementów (nie więcej niż M)
Konsument pobiera losową liczbę elementów (nie więcej niż M)
Zaimplementować rozwiązanie z losową liczbą porcji w dwóch wariantach:
Wariant naiwny: producent / konsument jest wstrzymywany aż w buforze nie będzie wystarczająco dużo miejsca / elementów.
Proszę uruchomić obydwa warianty algorytmu dla wielu producentów i konsumentów, a następnie zmierzyć i przedstawić na wykresie porównawczym (osobno dla producentów i konsumentów) średni czas oczekiwania na dostęp do bufora w zależności od wielkości porcji.

Uwagi:

Producenci i konsumenci losują wielkość porcji przed każdą operacją.
Pomiary proszę wykonać dla dwóch przypadków: (a) każda wielkość porcji jest losowana z równym prawdopodobieństwem; (b) prawdopodobieństwo wylosowania małej porcji jest dużo większe niż dużej porcji.
Do pomiaru czasu proszę używać System.nanoTime()
Proszę wykonać testy dla:
M równego 1000, 10k, 100k
Konfiguracji P-K: 10P+10K, 100P+100K, 1000P+1000K
Problem pochodzi z książki Z. Weiss, T. Gruzlewski, Programowanie wspolbiezne i rozproszone.