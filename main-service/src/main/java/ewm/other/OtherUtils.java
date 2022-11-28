package ewm.other;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ewm.exception.ValidationExc;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class OtherUtils {
    /**
     * Метод возвращает массив имён основных не нулевых полей объекта
     */
    public static String[] getNotNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> notEmptyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null) notEmptyNames.add(pd.getName());
        }

        String[] result = new String[notEmptyNames.size()];

        return notEmptyNames.toArray(result);
    }

    /**
     * Метод возвращает PageRequest, собранный на основе данных переданных в контролёр
     */
    public static Pageable pageableCreate(int from, int size, String... sortParam) {
        if (from < 0 || size <= 0) {
            throw new ValidationExc("Operation aborted.", "Wrong pagination parameters.");
        }

        int pageNumber = (int) (Math.ceil(((double) from + 1) / size) - 1);

        if (sortParam.length == 1 && !sortParam[0].isBlank()) {
            if (sortParam[0].equalsIgnoreCase("EVENT_DATE")) {
                return PageRequest.of(pageNumber, size, Sort.by("eventDate").ascending());
            } else if (sortParam[0].equalsIgnoreCase("VIEWS")) {
                return PageRequest.of(pageNumber, size, Sort.by("views").descending());
            } else if (sortParam[0].equalsIgnoreCase("INITIATOR_RATING")) {
                return PageRequest.of(pageNumber, size);
            }
            throw new ValidationExc("Operation aborted.", "Wrong sort parameters.");
        }

        return PageRequest.of(pageNumber, size);
    }
}
