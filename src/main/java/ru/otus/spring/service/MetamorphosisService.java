package ru.otus.spring.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import ru.otus.spring.domain.Butterfly;
import ru.otus.spring.domain.ButterflyActivity;
import ru.otus.spring.domain.Caterpillar;
import ru.otus.spring.domain.Cocoon;

@Service
public class MetamorphosisService {

    private static final Set<String> NIGHT_FLIERS = Set.of("Sphinx", "Noctua");

    public Cocoon pupate(Caterpillar caterpillar) {
        return new Cocoon(caterpillar.name(), caterpillar.species());
    }

    public ButterflyActivity activity(Cocoon cocoon) {
        return NIGHT_FLIERS.contains(cocoon.species()) ? ButterflyActivity.NIGHT : ButterflyActivity.DAY;
    }

    public Butterfly emergeDay(Cocoon cocoon) {
        return new Butterfly(cocoon.name(), cocoon.species(), "оранжево-чёрная", ButterflyActivity.DAY);
    }

    public Butterfly emergeNight(Cocoon cocoon) {
        return new Butterfly(cocoon.name(), cocoon.species(), "тускло-серая", ButterflyActivity.NIGHT);
    }

    public String announce(Butterfly butterfly) {
        String when = butterfly.activity() == ButterflyActivity.DAY ? "днём" : "ночью";
        return "Кокон раскрылся: бабочка '%s' (%s), окраска %s, активна %s".formatted(
                butterfly.name(), butterfly.species(), butterfly.colors(), when);
    }
}
