package egovframework.cmm;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** HH24MI 문자열·YYYYMMDD 문자열 변환 */
public final class TimeUtil {

	private static final DateTimeFormatter DE = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

	private TimeUtil() {
	}

	/** "0930" → 570. 형식이 틀리면 -1 */
	public static int toMinutes(String hhmi) {
		if (hhmi == null || !hhmi.matches("\\d{4}")) {
			return -1;
		}
		int h = Integer.parseInt(hhmi.substring(0, 2));
		int m = Integer.parseInt(hhmi.substring(2));
		if (h > 24 || m > 59 || (h == 24 && m > 0)) {
			return -1;
		}
		return h * 60 + m;
	}

	/** 570 → "0930" */
	public static String toHhmi(int minutes) {
		return String.format("%02d%02d", minutes / 60, minutes % 60);
	}

	/** "20261201" → LocalDate. 형식이 틀리거나 없는 날짜면 null */
	public static LocalDate parseDe(String de) {
		if (de == null || !de.matches("\\d{8}")) {
			return null;
		}
		try {
			return LocalDate.parse(de, DE);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	public static String toDe(LocalDate date) {
		return date.format(DE);
	}
}
