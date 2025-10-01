"""
/*
 * Escribe un programa que imprima los 50 primeros números de la sucesión
 * de Fibonacci empezando en 0.
 * - La serie Fibonacci se compone por una sucesión de números en
 *   la que el siguiente siempre es la suma de los dos anteriores.
 *   0, 1, 1, 2, 3, 5, 8, 13...
 */
"""
def fibonacci(n=50):
    fib=[0,1]
    if n <= 0:
        return []
    elif n==1:
        return [0]
    for i in range(2 , n):
        # print(fib[i-1])
        # print(fib[i-2])
        fib.append(fib[i-1] + fib[i-2])
    return fib

"""
/*
 * Escribe una función que reciba un texto y retorne verdadero o
 * falso (Boolean) según sean o no palíndromos.
 * Un Palíndromo es una palabra o expresión que es igual si se lee
  * de izquierda a derecha que de derecha a izquierda.
 * NO se tienen en cuenta los espacios, signos de puntuación y tildes.
 * Ejemplo: Ana lleva al oso la avellana.
 */
 [::-1]
"""

def palindromo(oracion):

    oracion=oracion.lower().replace(" ","")
    if oracion == oracion[::-1]:
        return True
    else:
        return False

def palindromo_dificil(oracion):
    oracion=oracion.lower().replace(" ","")
    alreves=""
    for i in range(1,(len(oracion)+1)):
        alreves+=oracion[len(oracion)-i]
    if oracion == alreves:
        return True
    else:
        return False


"""
/*
 * Escribe una función que calcule y retorne el factorial de un número dado
 * de forma recursiva.
 */
"""

def factorial(n):
    if n == 0 or n == 1:  # caso base
        return 1
    return n * factorial(n - 1)

if __name__ == "__main__":
    print(factorial(0))