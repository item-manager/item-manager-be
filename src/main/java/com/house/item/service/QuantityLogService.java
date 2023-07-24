package com.house.item.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.PurchaseItemServiceRQ;
import com.house.item.domain.QuantityLogDTO;
import com.house.item.domain.QuantityLogMallRS;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumByDate;
import com.house.item.domain.QuantityLogSumDTO;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.domain.QuantityLogSumsRQ;
import com.house.item.domain.QuantityLogsServiceRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentItemQuantityLogException;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import com.house.item.repository.ItemQuantityLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuantityLogService {
	private final ItemQuantityLogRepository quantityLogRepository;
	private final ItemService itemService;

	public ItemQuantityLog getQuantityLog(Long quantityLogNo, User user) throws NonExistentItemQuantityLogException {
		return quantityLogRepository.findByIdAndUser(quantityLogNo, user)
			.orElseThrow(() -> new NonExistentItemQuantityLogException(
				ExceptionCodeMessage.NON_EXISTENT_ITEM_QUANTITY_LOG.message()));
	}

	@Transactional
	public int purchaseItem(PurchaseItemServiceRQ request) {
		Item item = itemService.getItem(request.getItemId(), request.getUser());

		ItemQuantityLog quantityLog = ItemQuantityLog.builder()
			.item(item)
			.type(QuantityType.PURCHASE)
			.mall(request.getMall())
			.date(request.getDate())
			.price(request.getPrice())
			.count(request.getCount())
			.build();
		quantityLogRepository.save(quantityLog);

		item.addQuantity(request.getCount());

		return item.getQuantity();
	}

	@Transactional
	public int consumeItem(Long itemNo, ConsumeItemRQ consumeItemRQ, User user) {
		Item item = itemService.getItem(itemNo, user);
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

	public QuantityLogSearch getQuantityLogSearch(QuantityLogsServiceRQ request, Pageable pageable, User user) {
		Item item = itemService.getItem(request.getItemNo(), user);

		QuantityType type = null;
		if (request.getType() != null) {
			type = request.getType().getType();
		}

		return QuantityLogSearch.builder()
			.item(item)
			.type(type)
			.year(request.getYear())
			.month(request.getMonth())
			.pageable(pageable)
			.build();
	}

	public Page<QuantityLogDTO> getItemQuantityLogs(QuantityLogSearch quantityLogSearch) {
		return quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(quantityLogSearch);
	}

	public QuantityLogSumSearch getQuantityLogSumSearch(QuantityLogSumsRQ quantityLogSumsRQ, User user) {
		Item item = itemService.getItem(quantityLogSumsRQ.getItemNo(), user);

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

		List<QuantityType> searchType = new ArrayList<>();
		if (quantityLogSumSearch.getType() == null) {
			searchType = Arrays.stream(QuantityType.values()).toList();
		} else {
			searchType.add(quantityLogSumSearch.getType());
		}

		List<QuantityLogSumDTO> init = new ArrayList<>();
		if (quantityLogSumSearch.getYear() == null) {
			int year = LocalDate.now().getYear();
			for (int i = 10; i >= 0; i--) {
				for (QuantityType type : searchType) {
					init.add(
						QuantityLogSumDTO.builder()
							.date(year - i)
							.type(type)
							.sum(0)
							.build()
					);
				}
			}
		} else {
			for (int i = 1; i <= 12; i++) {
				for (QuantityType type : searchType) {
					init.add(
						QuantityLogSumDTO.builder()
							.date(i)
							.type(type)
							.sum(0)
							.build()
					);
				}
			}
		}

		List<QuantityLogSumDTO> sums = quantityLogRepository.sumByDate(quantityLogSumSearch);

		Map<String, QuantityLogSumDTO> filledSums = Stream.concat(sums.stream(), init.stream())
			.collect(Collectors.toMap(
				quantityLogSumDto -> String.valueOf(quantityLogSumDto.getDate()) + quantityLogSumDto.getType(),
				Function.identity(),
				(v1, v2) -> v1
			));

		return filledSums.values().stream()
			.sorted(Comparator.comparingInt(QuantityLogSumDTO::getDate))
			.collect(Collectors.groupingBy(
					QuantityLogSumDTO::getType,
					Collectors.mapping(
						dto -> QuantityLogSumByDate.builder()
							.date(dto.getDate())
							.sum(dto.getSum())
							.build(),
						Collectors.toList()
					)
				)
			);
	}

	public List<QuantityLogMallRS> getQuantityLogDistinctMalls(User user) {
		return quantityLogRepository.findDistinctMalls(user).stream()
			.map(mall -> QuantityLogMallRS.builder()
				.mall(mall)
				.build())
			.toList();
	}

	@Transactional
	public void deleteQuantityLog(Long quantityLogNo, User user) throws
		NonExistentItemQuantityLogException,
		SubtractCountExceedItemQuantityException {
		ItemQuantityLog quantityLog = getQuantityLog(quantityLogNo, user);

		Item item = quantityLog.getItem();
		if (quantityLog.getType() == QuantityType.PURCHASE) {
			item.subtractQuantity(quantityLog.getCount());
		} else if (quantityLog.getType() == QuantityType.CONSUME) {
			item.addQuantity(quantityLog.getCount());
		}

		quantityLogRepository.delete(quantityLog);
	}
}
