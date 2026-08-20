package bryanthedragon.morph.utils;

import net.minecraft.client.Minecraft;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;

public class Keys
{
    public static final String[] KEYS = new String[GLFW.GLFW_KEY_LAST + 1];
    public static final List<Integer> MODIFIERS = ImmutableList.<Integer>of(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_RIGHT_ALT);
    public static final String[] MODNAME = new String[] {"Ctrl", "Shift", "Alt"};

    public static String getKeyName(int key)
    {
        if (key < GLFW.GLFW_KEY_UNKNOWN || key >= GLFW.GLFW_KEY_LAST + 1)
        {
            return null;
        }

	// Adding this line prevents a null-pointer exception
        if (KEYS[key] == null)
        {
            KEYS[key] = getKey(key);
            if (KEYS[key] == null) {
                return "Unknown key";
            }
        }

        return KEYS[key];
    }

    private static String getKey(int key)
    {
        switch (key)
        {
            case GLFW.GLFW_KEY_MINUS:
                return "-";
            case GLFW.GLFW_KEY_EQUAL:
                return "=";
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                return "[";
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
                return "]";
            case GLFW.GLFW_KEY_SEMICOLON:
                return ";";
            case GLFW.GLFW_KEY_APOSTROPHE:
                return "'";
            case GLFW.GLFW_KEY_BACKSLASH:
                return "\\";
            case GLFW.GLFW_KEY_COMMA:
                return ",";
            case GLFW.GLFW_KEY_PERIOD:
                return ".";
            case GLFW.GLFW_KEY_SLASH:
                return "/";
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                return "`";
            case GLFW.GLFW_KEY_TAB:
                return "Tab";
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return "Caps Lock";
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return "L. Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return "L. Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT:
                return "L. Alt";
            case GLFW.GLFW_KEY_LEFT_SUPER:
                return Minecraft.ON_OSX ? "L. Cmd" : "L. Win";
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return "R. Shift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return "R. Ctrl";
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return "R. Alt";
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                return Minecraft.ON_OSX ? "R. Cmd" : "R. Win";
            case GLFW.GLFW_KEY_KP_DIVIDE:
                return "Numpad /";
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return "Numpad *";
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                return "Numpad -";
            case GLFW.GLFW_KEY_KP_ADD:
                return "Numpad +";
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return "Numpad .";
        }

        String name = GLFW.glfwGetKeyName(key, 0);

	// Adding this line prevents a null-pointer exception
	if (name == null) {
            return null;
        }

        if (name.length() > 1)
        {
            name = name.substring(0, 1) + name.substring(1).toLowerCase();
        }

        if (name.startsWith("Numpad"))
        {
            name = name.replace("Numpad", "Numpad ");
        }

        return name;
    }

    /* Combo keys */

    public static int getComboKeyCode(int[] held, int keyCode)
    {
        int comboKey = keyCode;
        int modifierIndex = MODIFIERS.indexOf(keyCode) % 3;

        if (held != null)
        {
            for (int heldKey : held)
            {
                int index = MODIFIERS.indexOf(heldKey) % 3;

                if (index >= 0 && index != modifierIndex)
                {
                    comboKey |= 1 << 31 - index;
                }
            }
        }

        return comboKey;
    }

    public static void main(String...args)
    {
        System.out.println(getComboKeyName(getComboKeyCode(new int[] {GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL}, GLFW.GLFW_KEY_RIGHT_ALT)));
    }

    public static int getMainKey(int comboKey)
    {
        int key = comboKey & 0x1FFFFFFF;

        if (key >= GLFW.GLFW_KEY_LAST)
        {
            key = GLFW.GLFW_KEY_UNKNOWN;
        }

        return key;
    }

    public static String getComboKeyName(int comboKey)
    {
        StringBuilder builder = new StringBuilder();
        int mainKey = getMainKey(comboKey);

        if (mainKey == GLFW.GLFW_KEY_UNKNOWN)
        {
            return getKeyName(mainKey);
        }

        for (int i = 0; i < 3; i++)
        {
            if ((comboKey & 1 << 31 - i) != 0)
            {
                builder.append(MODNAME[i]).append(" + ");
            }
        }

        builder.append(getKeyName(mainKey));

        return builder.toString();
    }

    public static boolean checkModifierKeys(int comboKey)
    {
        int index = MODIFIERS.indexOf(getMainKey(comboKey)) % 3;

        for (int i = 0; i < 3; i++)
        {
            if (i == index)
            {
                continue;
            }

            if ((comboKey & 1 << 31 - i) != 0 != isKeyDown(MODIFIERS.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isKeyDown(int key)
    {
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)
        {
            return GLFW.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || GLFW.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
        }
        else if (key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL)
        {
            return GLFW.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || GLFW.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
        else if (key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT)
        {
            return GLFW.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || GLFW.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
        }

        return GLFW.isKeyDown(key);
    }
}
