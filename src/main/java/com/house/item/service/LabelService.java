package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateLabel;
import com.house.item.domain.LabelRS;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.repository.LabelRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelService {

    private final LabelRepository labelRepository;
    private final AuthService authService;
    private final ItemService itemService;

    @Transactional
    public Long createLabel(CreateLabel createLabel) {
        User loginUser = authService.getLoginUser();
        validateDuplicationLabelName(createLabel.getName(), loginUser.getUserNo());

        Label label = Label.builder()
                .user(loginUser)
                .name(createLabel.getName())
                .build();

        labelRepository.save(label);
        return label.getLabelNo();
    }

    private void validateDuplicationLabelName(String name, Long userNo) throws NonUniqueLabelNameException {
        labelRepository.findByNameAndUserNo(name, userNo)
                .ifPresent(l -> {
                    throw new NonUniqueLabelNameException(ExceptionCodeMessage.NON_UNIQUE_LABEL_NAME.message());
                });
    }

    public Label getLabel(Long labelNo) throws NonExistentLabelException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return labelRepository.findByLabelNoAndUserNo(labelNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentLabelException(ExceptionCodeMessage.NON_EXISTENT_LABEL.message()));
    }

    @Transactional
    public ItemLabel attachLabelToItem(Long itemNo, Long labelNo) throws NonExistentItemException, NonExistentLabelException {
        Item item = itemService.getItem(itemNo);
        Label label = getLabel(labelNo);

        ItemLabel itemLabel = ItemLabel.builder()
                .item(item)
                .label(label)
                .build();

        List<ItemLabel> itemLabels = item.getItemLabels();
        if (!itemLabels.contains(itemLabel)) {
            itemLabels.add(itemLabel);
        }

        return itemLabel;
    }

}
