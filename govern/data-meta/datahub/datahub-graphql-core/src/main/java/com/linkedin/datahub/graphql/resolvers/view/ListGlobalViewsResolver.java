package com.linkedin.datahub.graphql.resolvers.view;

import com.google.common.collect.ImmutableList;
import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.AndFilterInput;
import com.linkedin.datahub.graphql.generated.DataHubView;
import com.linkedin.datahub.graphql.generated.DataHubViewType;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.FacetFilterInput;
import com.linkedin.datahub.graphql.generated.FilterOperator;
import com.linkedin.datahub.graphql.generated.ListGlobalViewsInput;
import com.linkedin.datahub.graphql.generated.ListViewsResult;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.Constants;
import com.linkedin.metadata.query.filter.Filter;
import com.linkedin.metadata.query.filter.SortCriterion;
import com.linkedin.metadata.query.filter.SortOrder;
import com.linkedin.metadata.search.SearchEntity;
import com.linkedin.metadata.search.SearchResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import static com.linkedin.datahub.graphql.resolvers.ResolverUtils.*;


/**
 * Resolver used for listing global DataHub Views.
 */
@Slf4j
public class ListGlobalViewsResolver implements DataFetcher<CompletableFuture<ListViewsResult>> {

  private static final String CREATED_AT_FIELD = "createdAt";
  private static final String VIEW_TYPE_FIELD = "type";
  private static final SortCriterion DEFAULT_SORT_CRITERION = new SortCriterion()
      .setField(CREATED_AT_FIELD)
      .setOrder(SortOrder.DESCENDING);
  private static final Integer DEFAULT_START = 0;
  private static final Integer DEFAULT_COUNT = 20;
  private static final String DEFAULT_QUERY = "";

  private final EntityClient _entityClient;

  public ListGlobalViewsResolver(@Nonnull final EntityClient entityClient) {
    _entityClient = Objects.requireNonNull(entityClient, "entityClient must not be null");
  }

  @Override
  public CompletableFuture<ListViewsResult> get(final DataFetchingEnvironment environment) throws Exception {

    final QueryContext context = environment.getContext();
    final ListGlobalViewsInput input = bindArgument(environment.getArgument("input"), ListGlobalViewsInput.class);

    return CompletableFuture.supplyAsync(() -> {
      final Integer start = input.getStart() == null ? DEFAULT_START : input.getStart();
      final Integer count = input.getCount() == null ? DEFAULT_COUNT : input.getCount();
      final String query = input.getQuery() == null ? DEFAULT_QUERY : input.getQuery();

      try {

        final SearchResult gmsResult = _entityClient.search(
                Constants.DATAHUB_VIEW_ENTITY_NAME,
                query,
                buildFilters(),
                DEFAULT_SORT_CRITERION,
                start,
                count,
                context.getAuthentication(),
                true);

        final ListViewsResult result = new ListViewsResult();
        result.setStart(gmsResult.getFrom());
        result.setCount(gmsResult.getPageSize());
        result.setTotal(gmsResult.getNumEntities());
        result.setViews(mapUnresolvedViews(gmsResult.getEntities().stream()
            .map(SearchEntity::getEntity)
            .collect(Collectors.toList())));
        return result;
      } catch (Exception e) {
        throw new RuntimeException("Failed to list global Views", e);
      }
    });
  }

  // This method maps urns returned from the list endpoint into Partial View objects which will be resolved be a separate Batch resolver.
  private List<DataHubView> mapUnresolvedViews(final List<Urn> entityUrns) {
    final List<DataHubView> results = new ArrayList<>();
    for (final Urn urn : entityUrns) {
      final DataHubView unresolvedView = new DataHubView();
      unresolvedView.setUrn(urn.toString());
      unresolvedView.setType(EntityType.DATAHUB_VIEW);
      results.add(unresolvedView);
    }
    return results;
  }

  private Filter buildFilters() {
    final AndFilterInput globalCriteria = new AndFilterInput();
    List<FacetFilterInput> andConditions = new ArrayList<>();
    andConditions.add(
        new FacetFilterInput(VIEW_TYPE_FIELD, null, ImmutableList.of(DataHubViewType.GLOBAL.name()), false, FilterOperator.EQUAL));
    globalCriteria.setAnd(andConditions);
    return buildFilter(Collections.emptyList(), ImmutableList.of(globalCriteria));
  }
}
