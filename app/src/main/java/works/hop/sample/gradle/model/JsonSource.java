package works.hop.sample.gradle.model;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

public class JsonSource {

    private JsonSource() {
        // utility functions class
    }

    public static void main(String[] args) throws IOException {
        AtomicObject user = new AtomicObject(null);
        user.put("firstName", new Atom(user, "jimmy"));
        System.out.println(Optional.ofNullable(user.get("firstName")).map(v -> v.getValue("firstName")));

        Atomic value = parse("/json/deep.json", new Stack<>(), new Stack<>());
        Atomic level1 = ((AtomicObject)value).get("level1");
        Atomic level10 = ((AtomicObject)level1).get("level10");
        Atomic floating = ((AtomicArray)level10).get(0);
        Atomic floating0 = ((AtomicObject)floating).get("floating");
        Atomic num0 = ((AtomicArray)floating0).get(0);
        System.out.println(num0.path());
    }

    public static Atomic parse(String file, Stack<Atomic> stack, Stack<String> fields) throws IOException {
        Atomic head = null;
        try (InputStream is = JsonSource.class.getResourceAsStream(file);
             JsonParser parser = Json.createParser(is)) {
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                switch (event) {
                    case START_ARRAY:
                        System.out.println(event);
                        if (fields.isEmpty()) {
                            head = new AtomicArray(head);
                        } else {
                            head = new AtomicArray(head, fields.peek());
                        }
                        stack.push(head);
                        break;
                    case START_OBJECT:
                        System.out.println(event);
                        if (fields.isEmpty()) {
                            head = new AtomicObject(head);
                        } else {
                            head = new AtomicObject(head, fields.peek());
                        }
                        stack.push(head);
                        break;
                    case END_ARRAY, END_OBJECT:
                        System.out.println(event);
                        head = stack.pop();
                        if (!fields.isEmpty()) {
                            if (stack.peek().isObject()) {
                                ((AtomicObject) stack.peek()).put(fields.pop(), head);
                            }
                            if (stack.peek().isArray()) {
                                ((AtomicArray) stack.peek()).add(head);
                            }
                        }
                        break;
                    case VALUE_FALSE:
                        System.out.println(event);
                        Atom no = new Atom(stack.peek(), false);
                        applyValue(stack, fields, head, no);
                        break;
                    case VALUE_NULL:
                        System.out.println(event);
                        Atom none = new Atom(stack.peek(), null);
                        applyValue(stack, fields, head, none);
                        break;
                    case VALUE_TRUE:
                        System.out.println(event);
                        Atom yes = new Atom(stack.peek(), true);
                        applyValue(stack, fields, head, yes);
                        break;
                    case KEY_NAME:
                        System.out.printf("%s %s - ", event, parser.getString());
                        fields.push(parser.getString());
                        break;
                    case VALUE_STRING:
                        System.out.printf("%s %s\n", event, parser.getString());
                        Atom string = new Atom(stack.peek(), parser.getString());
                        applyValue(stack, fields, head, string);
                        break;
                    case VALUE_NUMBER:
                        System.out.printf("%s %s\n", event, parser.getString());
                        Atom number = new Atom(stack.peek(), parser.getBigDecimal());
                        applyValue(stack, fields, head, number);
                        break;
                }
            }
        }
        return head;
    }

    private static void applyValue(Stack<Atomic> stack, Stack<String> fields, Atomic head, Atom number) {
        if (Objects.requireNonNull(head).isObject()) {
            if (!fields.isEmpty()) {
                ((AtomicObject) stack.peek()).put(fields.pop(), number);
            }
        } else if (Objects.requireNonNull(head).isArray()) {
            ((AtomicArray) stack.peek()).add(number);
        } else {
            System.out.println("Something is wrong. Check what's happening here");
        }
    }
}
