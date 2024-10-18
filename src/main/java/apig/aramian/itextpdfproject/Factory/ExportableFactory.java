package apig.aramian.itextpdfproject.Factory;

import java.util.List;
import java.util.Map;

public class ExportableFactory {
    public static Exportable createExportable(Object value) {
        return switch (value) {
            case Map map -> new MapExportable((Map<String, Object>) value);
            case List list -> new ListExportable((List<Object>) value);
            case String s when s.startsWith("data:image/png;base64,") -> new ImageExportable(s);
            case null, default -> new StringExportable(value != null ? value.toString() : "null");
        };
    }
}
