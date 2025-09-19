# EJERCICIO 1:
# Isograma
# Un isograma es una palabra que no tiene letras repetidas. Consideraciones Adicionales:
# Un string vacio es un isograma.
# Si el string tiene mas de una palabra retornar false.
#TESTING
#"" true
#"perro" false
#"como estas" false
#"gato" true
# "caro" true

def isograma(palabra):
    lista=[]
    for letra in palabra:
        if (letra in lista):
            return False
        elif (letra == " "):
            return False
        else:
            lista.append(letra)
    return True

print(isograma("caro"))