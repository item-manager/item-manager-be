package com.house.item.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.Page;
import com.house.item.domain.PurchaseItemRQ;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumByDate;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.domain.QuantityLogSumsRQ;
import com.house.item.domain.QuantityLogsRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.exception.NonExistentItemQuantityLogException;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import com.house.item.repository.ItemQuantityLogRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuantityLogService {
	private final ItemQuantityLogRepository quantityLogRepository;
	private final ItemService itemService;

	public ItemQuantityLog getQuantityLog(Long quantityLogNo) throws NonExistentItemQuantityLogException {
		SessionUser sessionUser = (SessionUser)SessionUtils.getAttribute(SessionConst.LOGIN_USER);
		ItemQuantityLog log = quantityLogRepository.findById(quantityLogNo)
			.orElseThrow(() -> new NonExistentItemQuantityLogException(
				ExceptionCodeMessage.NON_EXISTENT_ITEM_QUANTITY_LOG.message()));

		if (log.getItem().getUser().getUserNo().equals(sessionUser.getUserNo())) {
			return log;
		}
		throw new NonExistentItemQuantityLogException(
			ExceptionCodeMessage.NON_EXISTENT_ITEM_QUANTITY_LOG.message());
	}

	@Transactional
	public int purchaseItem(Long itemNo, PurchaseItemRQ purchaseItemRQ) {
		Item item = itemService.getItem(itemNo);

		ItemQuantityLog quantityLog = ItemQuantityLog.builder()
			.item(item)
			.type(QuantityType.PURCHASE)
			.mall(purchaseItemRQ.getMall())
			.date(purchaseItemRQ.getDate())
			.price(purchaseItemRQ.getUnitPrice())
			.count(purchaseItemRQ.getCount())
			.build();
		quantityLogRepository.save(quantityLog);

		item.addQuantity(purchaseItemRQ.getCount());

		return item.getQuantity();
	}

	@Transactional
	public int consumeItem(Long itemNo, ConsumeItemRQ consumeItemRQ) {
		Item item = itemService.getItem(itemNo);
		if (item.getQuantity() < consumeItemRQ.getCount()) {
			throw new SubtractCountExceedItemQuantityException(
				ExceptionCodeMessage.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION.message());
		}

		ItemQuantityLog quantityLog = ItemQuantityLog.builder()
			.item(item)
			.type(QuantityType.CONSUME)
			.date(consumeItemRQ.getDate())
			.count(consumeItemRQ.getCount())
			.build();
		quantityLogRepository.save(quantityLog);

		item.subtractQuantity(consumeItemRQ.getCount());

		return item.getQuantity();
	}

	public QuantityLogSearch getQuantityLogSearch(QuantityLogsRQ quantityLogsRQ) {
		Item item = itemService.getItem(quantityLogsRQ.getItemNo());

		Map<String, String> sortMapping = new HashMap<>();
		sortMapping.put("+", "ASC");
		sortMapping.put("-", "DESC");

		QuantityType type = null;
		if (quantityLogsRQ.getType() != null) {
			type = quantityLogsRQ.getType().getType();
		}

		return QuantityLogSearch.builder()
			.item(item)
			.type(type)
			.year(quantityLogsRQ.getYear())
			.month(quantityLogsRQ.getMonth())
			.orderBy(quantityLogsRQ.getOrderBy().getColumn())
			.sort(sortMapping.get(quantityLogsRQ.getSort()))
			.page(quantityLogsRQ.getPage())
			.size(quantityLogsRQ.getSize())
			.build();
	}

	public List<ItemQuantityLog> getItemQuantityLogs(QuantityLogSearch quantityLogSearch) {
		return quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(quantityLogSearch);
	}

	public Page getItemQuantityLogsPage(QuantityLogSearch quantityLogSearch) {
		int rowCount = Math.toIntExact(quantityLogRepository.getLogsByItemNoRowCount(quantityLogSearch));

		int size = quantityLogSearch.getSize();
		int totalPage = rowCount / size;
		if (rowCount % size > 0) {
			totalPage++;
		}

		return Page.builder()
			.totalDataCnt(rowCount)
			.totalPages(totalPage)
			.requestPage(quantityLogSearch.getPage())
			.requestSize(size)
			.build();
	}

	public QuantityLogSumSearch getQuantityLogSumSearch(QuantityLogSumsRQ quantityLogSumsRQ) {
		Item item = itemService.getItem(quantityLogSumsRQ.getItemNo());

		QuantityType type = null;
		if (quantityLogSumsRQ.getType() != null) {
			type = quantityLogSumsRQ.getType().getType();
		}

		return QuantityLogSumSearch.builder()
			.item(item)
			.year(quantityLogSumsRQ.getYear())
			.type(type)
			.build();
	}

	public Map<QuantityType, List<QuantityLogSumByDate>> getItemQuantityLogSumByDate(
		QuantityLogSumSearch quantityLogSumSearch) {
		List<QuantityLogSumDto> sums = quantityLogRepository.sumByDate(quantityLogSumSearch);

		Map<QuantityType, List<QuantityLogSumByDate>> logSumByType = separateLogSumByType(sums,
			quantityLogSumSearch.getType());

		fillInLogSumWithDate(logSumByType, quantityLogSumSearch.getYear());

		return logSumByType;
	}

	private Map<QuantityType, List<QuantityLogSumByDate>> separateLogSumByType(List<QuantityLogSumDto> sums,
		QuantityType searchType) {
		Map<QuantityType, List<QuantityLogSumByDate>> sumByType = new HashMap<>();
		if (searchType == null) {
			for (QuantityType type : QuantityType.values()) {
				sumByType.put(type, new ArrayList<>());
			}
		} else {
			sumByType.put(searchType, new ArrayList<>());
		}

		for (QuantityLogSumDto sum : sums) {
			sumByType.get(sum.getType())
				.add(
					QuantityLogSumByDate.builder()
						.date(sum.getDate())
						.sum(sum.getSum())
						.build()
				);
		}

		return sumByType;
	}

	private Map<QuantityType, List<QuantityLogSumByDate>> fillInLogSumWithDate(
		Map<QuantityType, List<QuantityLogSumByDate>> logSumByType, Integer searchYear) {
		for (QuantityType type : logSumByType.keySet()) {
			List<QuantityLogSumByDate> sumByDates = logSumByType.get(type);

			if (searchYear == null) {
				int thisYear = LocalDateTime.now().getYear();

				boolean isInDate;
				for (int y = thisYear; y >= thisYear - 20; y--) {
					isInDate = false;
					for (QuantityLogSumByDate sumByDate : sumByDates) {
						if (y == sumByDate.getDate()) {
							isInDate = true;
							break;
						}
					}
					if (!isInDate) {
						sumByDates.add(
							QuantityLogSumByDate.builder()
								.date(y)
								.sum(0)
								.build()
						);
					}
				}

				sumByDates.sort((s1, s2) -> s2.getDate() - s1.getDate());
			} else {
				boolean isInDate;
				for (int m = 1; m <= 12; m++) {
					isInDate = false;
					for (QuantityLogSumByDate sumByDate : sumByDates) {
						if (m == sumByDate.getDate()) {
							isInDate = true;
							break;
						}
					}
					if (!isInDate) {
						sumByDates.add(
							QuantityLogSumByDate.builder()
								.date(m)
								.sum(0)
								.build()
						);
					}
				}
			}

			sumByDates.sort(Comparator.comparingInt(QuantityLogSumByDate::getDate));
		}

		return logSumByType;
	}

	@Transactional
	public void deleteQuantityLog(Long quantityLogNo) throws
		NonExistentItemQuantityLogException,
		SubtractCountExceedItemQuantityException {
		ItemQuantityLog quantityLog = getQuantityLog(quantityLogNo);

		Item item = quantityLog.getItem();
		if (quantityLog.getType() == QuantityType.PURCHASE) {
			item.subtractQuantity(quantityLog.getCount());
		} else if (quantityLog.getType() == QuantityType.CONSUME) {
			item.addQuantity(quantityLog.getCount());
		}

		quantityLogRepository.delete(quantityLog);
	}
}
