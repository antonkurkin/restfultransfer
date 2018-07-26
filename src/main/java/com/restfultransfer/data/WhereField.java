package com.restfultransfer.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class WhereField {
	public final String FieldName;
	public WhereField(String FieldName) {
		this.FieldName = FieldName;
	}
	abstract void SetField(int n, PreparedStatement sqlStatement) throws SQLException;
}
