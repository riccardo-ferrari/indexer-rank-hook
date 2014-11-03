package com.liferay.kickstart.indexer.hook;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

public class JournalArticleCustomIndexerPostPorcessorImpl implements
		IndexerPostProcessor {

	@Override
	public void postProcessContextQuery(BooleanQuery contextQuery,
			SearchContext searchContext) throws Exception {
		_log.debug("postProcessContextQuery");
		addSortByPriority(searchContext);
	}

	@Override
	public void postProcessDocument(Document document, Object obj)
			throws Exception {
		JournalArticle journalArticle = (JournalArticle) obj;
		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(JournalArticle.class.getName(), journalArticle.getResourcePrimKey());
		if (assetEntry == null) return;
		List<AssetCategory> categories = assetEntry.getCategories();
		for (AssetCategory assetCategory : categories) {
			if (assetCategory.getName().equals("liferay")) {
				document.getField(Field.CONTENT).setBoost(3.0f);
				document.getField(Field.TITLE).setBoost(5.0f);
				Field priority = document.getField(Field.PRIORITY);
				priority.setValue("100");
				priority.setBoost(10.0f);
			}
		}
	}

	@Override
	public void postProcessFullQuery(BooleanQuery fullQuery,
			SearchContext searchContext) throws Exception {
		_log.debug("postProcessFullQuery");
	}

	@Override
	public void postProcessSearchQuery(BooleanQuery searchQuery,
			SearchContext searchContext) throws Exception {
		_log.debug("postProcessSearchQuery");

	}

	@Override
	public void postProcessSummary(Summary summary, Document document,
			Locale locale, String snippet, PortletURL portletURL) {
		_log.debug("postProcessSummary");
	}
	
	protected SearchContext addSortByPriority(SearchContext searchContext) {
		Sort[] sorts = searchContext.getSorts();
		if (sorts == null) {
			sorts = new Sort[0];
		}
		Sort score = new Sort(Field.PRIORITY, true);
		sorts = ArrayUtil.append(sorts, score);			
		searchContext.setSorts(sorts);
		
		return searchContext;
	}

	Log _log = LogFactoryUtil.getLog(getClass());
}
