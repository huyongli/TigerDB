package cn.ittiger.database.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import android.database.Cursor;

import cn.ittiger.database.manager.FieldTypeManager;
import cn.ittiger.database.util.DateUtil;
import cn.ittiger.database.util.ValueUtil;

/**
 * 实体属性字段
 * @author: huylee
 * @time:	2015-8-13下午10:52:38
 */
public class Property {
	/**
	 * 字段名，建表时用
	 */
	private String column;
	/**
	 * 默认值，建表时要设置的字段默认值
	 */
	private String defaultValue;
	/**
	 * 该字段对应实体信息中的属性字段信息
	 */
	private Field field;
	/**
	 * 该字段的get方法反射对象
	 */
	private Method get;
	/**
	 * 该字段的set方法反射对象
	 */
	private Method set;
	
	/**
	 * 获取指定对象的当前字段的值
	 * Author: hyl
	 * Time: 2015-8-16下午10:23:35
	 * @param entity	获取字段值的对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Object entity) {
		if(entity != null || get != null) {
			try {
				return (T) get.invoke(entity);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 设置指定对象的当前字段的值
	 * Author: hyl
	 * Time: 2015-8-16下午10:24:08
	 * @param entity	要设置字段值的对象
	 * @param value		要设置的值
	 * @throws Exception 
	 */
	public void setValue(Object entity, Object value) throws Exception {
		int fieldType = FieldTypeManager.getFieldType(field);
		try {
			switch(fieldType) {
				case FieldTypeManager.BASE_TYPE_BOOLEAN:
					set.invoke(entity, Boolean.parseBoolean(value.toString()));
					break;
				case FieldTypeManager.BASE_TYPE_BYTE_ARRAY:
					set.invoke(entity, (byte[])value);
					break;
				case FieldTypeManager.BASE_TYPE_CHAR:
					set.invoke(entity, value.toString().charAt(0));
					break;
				case FieldTypeManager.BASE_TYPE_STRING:
					set.invoke(entity, value.toString());
					break;
				case FieldTypeManager.BASE_TYPE_DATE:
					set.invoke(entity, DateUtil.formatDatetime((Date) value));
					break;
				case FieldTypeManager.BASE_TYPE_DOUBLE:
					set.invoke(entity, Double.parseDouble(value.toString()));
					break;
				case FieldTypeManager.BASE_TYPE_FLOAT:
					set.invoke(entity, Float.parseFloat(value.toString()));
					break;
				case FieldTypeManager.BASE_TYPE_INT:
					set.invoke(entity, Integer.parseInt(value.toString()));
					break;
				case FieldTypeManager.BASE_TYPE_LONG:
					set.invoke(entity, Long.parseLong(value.toString()));
					break;
				case FieldTypeManager.BASE_TYPE_SHORT:
					set.invoke(entity, Short.parseShort(value.toString()));
					break;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 设置指定实体对象当前属性字段的值
	 * Author: hyl
	 * Time: 2015-8-21上午10:20:14
	 * @param entity		要设置值的实体对象
	 * @param cursor		数据来源
	 * @throws Exception 
	 */
	public void setValue(Object entity, Cursor cursor) throws Exception {
		int fieldType = FieldTypeManager.getFieldType(field);
		try {
			int culomnIdx = cursor.getColumnIndex(column);
			String columnValue = cursor.getString(culomnIdx);
			boolean isEmpty = ValueUtil.isEmpty(columnValue);
			switch(fieldType) {
				case FieldTypeManager.BASE_TYPE_BOOLEAN:
					set.invoke(entity, isEmpty ? false : Boolean.parseBoolean(columnValue));
					break;
				case FieldTypeManager.BASE_TYPE_BYTE_ARRAY:
					set.invoke(entity, cursor.getBlob(culomnIdx));
					break;
				case FieldTypeManager.BASE_TYPE_CHAR:
					set.invoke(entity, isEmpty ? Character.valueOf(' ') : columnValue.charAt(0));
					break;
				case FieldTypeManager.BASE_TYPE_STRING:
					set.invoke(entity, columnValue);
					break;
				case FieldTypeManager.BASE_TYPE_DATE:
					
					set.invoke(entity, isEmpty ? "" : DateUtil.parseDatetime(columnValue));
					break;
				case FieldTypeManager.BASE_TYPE_DOUBLE:
					set.invoke(entity, cursor.getDouble(culomnIdx));
					break;
				case FieldTypeManager.BASE_TYPE_FLOAT:
					set.invoke(entity, cursor.getFloat(culomnIdx));
					break;
				case FieldTypeManager.BASE_TYPE_INT:
					set.invoke(entity, cursor.getInt(culomnIdx));
					break;
				case FieldTypeManager.BASE_TYPE_LONG:
					set.invoke(entity, cursor.getLong(culomnIdx));
					break;
				case FieldTypeManager.BASE_TYPE_SHORT:
					set.invoke(entity, cursor.getShort(culomnIdx));
					break;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Field getField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}

	public Method getGet() {
		return get;
	}

	public void setGet(Method get) {
		this.get = get;
	}

	public Method getSet() {
		return set;
	}

	public void setSet(Method set) {
		this.set = set;
	}
}
