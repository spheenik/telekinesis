package telekinesis.model.steam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SteamId {

    public static final long DEFAULT = 0x110000100000000L;

    private static Pattern ID_REGEX = Pattern.compile("STEAM_(?<universe>[0-5]):(?<authserver>[0-1]):(?<accountid>\\d+)", Pattern.CASE_INSENSITIVE);

    private static final int NUM_BITS = 32;
    private static final int INSTANCE_BITS = 20;
    private static final int TYPE_BITS = 4;
    private static final int UNIVERSE_BITS = 8;

    private static final int NUM_SHIFT = 0;
    private static final int INSTANCE_SHIFT = NUM_SHIFT + NUM_BITS;
    private static final int TYPE_SHIFT = INSTANCE_SHIFT + INSTANCE_BITS;
    private static final int UNIVERSE_SHIFT = TYPE_SHIFT + TYPE_BITS;

    private static final long NUM_MASK = ((1L << NUM_BITS) - 1);
    private static final long INSTANCE_MASK = ((1L << INSTANCE_BITS) - 1);
    private static final long TYPE_MASK = ((1L << TYPE_BITS) - 1);
    private static final long UNIVERSE_MASK = ((1L << UNIVERSE_BITS) - 1);


    private final long id;

    public SteamId() {
        this(0L, 1, EAccountType.Individual, EUniverse.Public);
    }

    public SteamId(long id) {
        this.id = id;
    }

    public SteamId(long num, int instance, EAccountType type, EUniverse universe) {
        this.id =
                ((num & NUM_MASK) << NUM_SHIFT)
                        | (((long) instance & INSTANCE_MASK) << INSTANCE_SHIFT)
                        | (((long) type.v() & TYPE_MASK) << TYPE_SHIFT)
                        | (((long) universe.v() & UNIVERSE_MASK) << UNIVERSE_SHIFT)
        ;
    }

    public SteamId(String steamId) {
        final Matcher m = ID_REGEX.matcher(steamId);
        if (!m.matches()) {
            throw new IllegalArgumentException();
        }
        this.id =
                (((Long.parseLong(m.group("accountid")) << 1) | Long.parseLong(m.group("authserver")) & NUM_MASK) << NUM_SHIFT)
                        | ((1L & INSTANCE_MASK) << INSTANCE_SHIFT)
                        | (((long) EAccountType.Individual.v() & TYPE_MASK) << TYPE_SHIFT)
                        | ((Long.parseLong(m.group("universe")) & UNIVERSE_MASK) << UNIVERSE_SHIFT)
        ;
    }

    public long toLong() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SteamID[");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }


    public static void main(String[] args) {
        System.out.println(Long.toHexString(new SteamId().toLong()));
    }
}
