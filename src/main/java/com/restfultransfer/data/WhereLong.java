package com.restfultransfer.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereLong extends WhereField {
	long l;
	public WhereLong(String FieldName, long l) {
		super(FieldName);
		this.l = l;
	}
	void SetField(int n, PreparedStatement sqlStatement) throws SQLException { sqlStatement.setLong(n, l); }
}
