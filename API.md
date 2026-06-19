# MTOT API — Mod Integration Guide

MTOT (More Than One Touch) allows your mod to register custom key combination actions through a simple static API.

## Getting the API

```java
import com.mtot.api.IMTOTAPI;
import com.mtot.api.MTOTAPI;

IMTOTAPI api = MTOTAPI.getInstance();
```

## Registering an Action

Register an action with a default key combination:

```java
import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

KeyCombination combo = KeyCombination.of(GLFW_KEY_R, Modifier.CONTROL, Modifier.SHIFT);
api.register("my_mod:my_action", combo, () -> {
    System.out.println("Action triggered!");
});
```

## Registering Without a Default Binding

```java
api.registerAction("my_mod:optional_action", () -> {
    // user must bind via GUI
});
```

## Binding at Runtime

```java
api.bind("my_mod:my_action", KeyCombination.of(GLFW_KEY_T, Modifier.ALT));
```

## Checking for Conflicts

```java
List<String> conflicts = api.findConflicts("my_mod:my_action", newCombo);
if (!conflicts.isEmpty()) {
    // warn user: combo already used by conflicts.get(0)
}
```

## Getting Current Bindings

```java
KeyCombination combo = api.getBinding("my_mod:my_action").orElse(null);
```

Make sure your action IDs use a unique namespace (e.g., `your_mod_id:action_name`) to avoid collisions.

The API is available after MTOT initialises (during client startup).
