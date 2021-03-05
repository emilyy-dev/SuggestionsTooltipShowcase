package com.github.fefo.suggestionstooltipshowcase;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionHelper {

  private static final String NMS_CLASS_FORMAT;
  private static final Method JSON_TO_COMPONENT_METHOD;

  // PacketPlayOutTabComplete suggestions fields
  private static final Field SUGGESTIONS_FIELD;

  static {
    try {
      final Class<?> craftServerClass = Bukkit.getServer().getClass();
      final String nmsVersion = craftServerClass.getPackage().getName().split("\\.")[3];
      NMS_CLASS_FORMAT = "net.minecraft.server." + nmsVersion + ".%s";

      final Class<?> chatSerializerClass = getNmsClass("IChatBaseComponent$ChatSerializer");
      JSON_TO_COMPONENT_METHOD = chatSerializerClass.getDeclaredMethod("jsonToComponent", String.class);

      final Class<?> packetPlayOutTabCompleteClass = getNmsClass("PacketPlayOutTabComplete");
      SUGGESTIONS_FIELD = packetPlayOutTabCompleteClass.getDeclaredField("b");
      SUGGESTIONS_FIELD.setAccessible(true);
    } catch (final Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  public static Suggestions getSuggestions(final Object packet) throws ReflectiveOperationException {
    return (Suggestions) SUGGESTIONS_FIELD.get(packet);
  }

  public static Message messageFromComponent(final ComponentLike component) {
    try {
      final String serialized = GsonComponentSerializer.gson().serialize(component.asComponent());
      return (Message) JSON_TO_COMPONENT_METHOD.invoke(null, serialized);
    } catch (final ReflectiveOperationException exception) {
      return new LiteralMessage(PlainComponentSerializer.plain().serialize(component.asComponent()));
    }
  }

  private static Class<?> getNmsClass(final String name) throws ReflectiveOperationException {
    return Class.forName(String.format(NMS_CLASS_FORMAT, name));
  }
}
