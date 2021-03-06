package com.micmiu.mvc.ferriswheel.support.easyui;

import com.micmiu.mvc.ferriswheel.core.annotation.ShowParam;
import com.micmiu.mvc.ferriswheel.support.easyui.vo.GridColumn;
import com.micmiu.mvc.ferriswheel.support.easyui.vo.PropertyGridData;
import com.micmiu.mvc.ferriswheel.support.jackson2.Jackson2ExtMapper;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created
 * User: <a href="http://micmiu.com">micmiu</a>
 * Date: 11/11/2015
 * Time: 15:48
 */
public class EasyuiBeanUtils {
	/**
	 * 根据实体类中@showparam注释转化为griddata表头信息
	 *
	 * @param clazz
	 * @param messageSource
	 * @param locale
	 * @return
	 */
	public static List<GridColumn> getBeanColumns(Class<?> clazz, MessageSource messageSource, Locale locale) {
		List<GridColumn> columns = new ArrayList<GridColumn>();
		Field fields[] = clazz.getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();
			if (!field.isAnnotationPresent(ShowParam.class)) {
				continue;
			}
			GridColumn column = new GridColumn();
			column.setField(fieldName);
			String cname = field.getAnnotation(ShowParam.class).value();
			column.setTitle(cname);
			column.setAlign(field.getAnnotation(ShowParam.class).align());
			column.setSortable(field.getAnnotation(ShowParam.class).sortable());
			column.setWidth(field.getAnnotation(ShowParam.class).width());
			columns.add(column);
		}

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(ShowParam.class)) {
				continue;
			}

			String name = method.getName();
			if (!name.startsWith("get")) {
				continue;
			}

			String fieldName = StringUtils.uncapitalize(name.substring(3));

			GridColumn column = new GridColumn();
			column.setField(fieldName);
			String cname = method.getAnnotation(ShowParam.class).value();
			column.setTitle(cname);
			column.setAlign(method.getAnnotation(ShowParam.class).align());
			column.setSortable(method.getAnnotation(ShowParam.class).sortable());
			column.setWidth(method.getAnnotation(ShowParam.class).width());
			columns.add(column);

		}

		return columns;

	}


	public static List<PropertyGridData> convertPropertyGridData(Object bean) {
		return convertPropertyGridData(bean, null);
	}

	public static List<PropertyGridData> convertPropertyGridData(Object bean, Map<String, String> showMap) {
		List<PropertyGridData> volist = new ArrayList<PropertyGridData>();
		try {
			Jackson2ExtMapper mapper = new Jackson2ExtMapper(null, new SimpleDateFormat("yyyy-MM-dd"));
			JSONObject obj = JSONObject.fromObject(mapper.serJson(bean));
			Iterator it = obj.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object valObj = obj.get(key);
				String value = null;
				if (null == valObj
						|| (valObj instanceof JSONObject && ((JSONObject) valObj)
						.isNullObject())) {
					value = "";
				} else {
					value = valObj.toString();
				}

				if (null == showMap) {
					volist.add(new PropertyGridData(key, value));
				} else {
					if (null != showMap.get(key)) {
						volist.add(new PropertyGridData(showMap.get(key), value));
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return volist;
	}


}
