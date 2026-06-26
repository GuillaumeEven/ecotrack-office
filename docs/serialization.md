# Serialización en Java

## ¿Qué es la serialización?

La **serialización** es el proceso que convierte un objeto Java en una secuencia de bytes para:
- **Guardarlo** en disco
- **Transmitirlo** a través de una red
- **Almacenarlo** en memoria en forma binaria

La operación inversa se llama **deserialización**: reconvertir los bytes en un objeto Java.

### Ejemplo típico

```java
// Serialización: objeto → bytes
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.bin"));
oos.writeObject(miObjeto);
oos.close();

// Deserialización: bytes → objeto
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.bin"));
MiClase miObjeto = (MiClase) ois.readObject();
ois.close();
```

## serialVersionUID: El número mágico

### ¿Qué es?

`serialVersionUID` es un **identificador de versión** que Java utiliza para validar la compatibilidad entre la serialización y la deserialización.

```java
private static final long serialVersionUID = 1L;
```

### ¿Cómo funciona?

Cuando Java **serializa** un objeto:
1. Guarda el estado del objeto (sus campos)
2. También guarda `serialVersionUID` en el archivo

Cuando Java **deserializa**:
1. Carga el `serialVersionUID` del archivo
2. Lo compara con el `serialVersionUID` de la clase actual
3. **Si los números no coinciden** → Excepción `InvalidClassException` y crash ❌

### Escenario concreto

```java
// v1.0 de la clase
class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    public String nombre;
    public int edad;
}
// Serializas: Usuario("Alice", 30) → guardado con serialVersionUID = 1L
```

```java
// v2.0 - añades un campo
class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;  // ⚠️ ¡MISMO número!
    public String nombre;
    public int edad;
    public String email;  // ← campo nuevo
}
// Intentas deserializar... ¡CRASH!
// La clase cambió, pero el número no fue actualizado
```

**Solución**: Incrementa el `serialVersionUID`

```java
class Usuario implements Serializable {
    private static final long serialVersionUID = 2L;  // ← número aumentado
    public String nombre;
    public int edad;
    public String email;
}
```

## ¿Y para los DTOs JSON REST?

En nuestro proyecto, los DTOs (Data Transfer Objects) como `ErrorResponse` se usan principalmente para:
- **Serializar en JSON** vía Jackson (respuestas HTTP)
- **Deserializar desde JSON** (solicitudes HTTP)

La serialización **binaria** Java (`ObjectOutputStream`) casi nunca se usa en una API REST moderna.

**Entonces: ¿por qué mantener `serialVersionUID`?**

✅ **Es "cero problema"** porque:
- No rompe nada
- Es una buena práctica Java (SonarQube lo recomienda)
- Prepara el código en caso de que alguien quiera serializar en binario
- Los IDEs a menudo generan este campo automáticamente

❌ **Podríamos eliminarlo**, pero honestamente es inútil aquí.

## Resumen

| Aspecto | Detalle |
|---------|---------|
| **Serialización** | Convertir un objeto Java → bytes (archivo, red, etc.) |
| **serialVersionUID** | Número de versión para validar compatibilidad |
| **Mismatch** | Si la clase cambia y `serialVersionUID` no cambia → **crash** |
| **DTOs REST** | El `serialVersionUID` es útil solo para serialización **binaria**, no JSON |
| **EcoTrack** | Lo mantenemos por convención, cero riesgo |
