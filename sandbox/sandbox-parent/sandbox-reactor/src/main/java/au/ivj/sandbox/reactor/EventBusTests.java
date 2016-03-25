package au.ivj.sandbox.reactor;

import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static reactor.bus.selector.Selectors.$;

/**
 * Created by Itamar on 21/03/2016.
 */
public class EventBusTests {
    private static Map<Integer, String> repository;

    static {
        repository = new TreeMap<>();
        repository.put(1, "First");
    }

    public static void main(String... args) throws InterruptedException {
        EventBus bus = EventBus.create();

        bus.receive($("commands/search"), e -> repository.entrySet().stream().filter(ee -> ee.getValue().contains(e.getData().toString())).collect(Collectors.toList()));
        bus.on($("commands/findReply"), e -> System.out.println("Replied: " + e.getData()));
        bus.on($("commands/find"), e -> bus.notify(e.getReplyTo(), Event.wrap(repository.get((Integer) e.getData()))));
        bus.on($("commands/add"), e -> repository.put(repository.size() + 1, e.getData().toString()));

        bus.notify("commands/find", Event.wrap(1, "commands/findReply"));
        bus.notify("commands/add", Event.wrap("Birst"));
        bus.sendAndReceive("commands/search", Event.wrap("irst"), e -> ((List<Map.Entry>) e.getData()).forEach(ee -> System.out.println(ee.getValue())));


        while (true) {
            Thread.sleep(1000);
        }
    }
}
