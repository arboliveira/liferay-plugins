/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.solr.query;

import com.liferay.portal.kernel.search.QueryPreProcessConfiguration;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import org.apache.lucene.index.Term;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 * @author Miguel Angelo Caldas Gallindo
 */
public class TermQueryTranslatorImpl implements TermQueryTranslator {

	@Reference
	public void setQueryPreProcessConfiguration(
		QueryPreProcessConfiguration queryPreProcessConfiguration) {

		_queryPreProcessConfiguration = queryPreProcessConfiguration;
	}

	@Override
	public org.apache.lucene.search.Query translate(TermQuery termQuery) {
		QueryTerm queryTerm = termQuery.getQueryTerm();

		String field = queryTerm.getField();
		String value = queryTerm.getValue();

		if (_queryPreProcessConfiguration.isSubstringSearchAlways(field)) {
			return _toCaseInsensitiveSubstringQuery(field, value);
		}

		return new org.apache.lucene.search.TermQuery(new Term(field, value));
	}

	private org.apache.lucene.search.Query _toCaseInsensitiveSubstringQuery(
		String field, String value) {

		value = StringUtil.replace(value, StringPool.PERCENT, StringPool.BLANK);
		value = StringUtil.toLowerCase(value);
		value = StringPool.STAR + value + StringPool.STAR;

		return new org.apache.lucene.search.WildcardQuery(new Term(
			field, value));
	}

	private QueryPreProcessConfiguration _queryPreProcessConfiguration;

}