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
        notEmptyNames.add("userIdHeader");
        String[] result = new String[notEmptyNames.size()];

        return notEmptyNames.toArray(result);
    }

    /**
     * Метод возвращает PageRequest, собранный на основе данных переданных в контролёр
     */
    public static Pageable pageableCreate(int from, int size, String... sortParam) {
        if (from < 0 || size <= 0) {
            throw new ValidationExc("Wrong pagination parameters. Operation aborted.");
        }

        int pageNumber = (int) (Math.ceil(((double) from + 1) / size) - 1);

        if (sortParam.length == 1 && !sortParam[0].isBlank()) {
            return PageRequest.of(pageNumber, size, Sort.by(sortParam[0]).descending());
        }

        return PageRequest.of(pageNumber, size);
    }
}
