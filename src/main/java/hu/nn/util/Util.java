package hu.nn.util;

import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

	public static boolean isEmpty(Object obj) {
		if (obj instanceof CharSequence) {
			obj = ((String) obj).trim();
		}

		return ObjectUtils.isEmpty(obj);
	}

	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

}
