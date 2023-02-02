package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateLabel;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelService {

    private final LabelRepository labelRepository;
    private final AuthService authService;

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

    public Label getLabel(Long labelNo) {
        return labelRepository.findOne(labelNo)
                .orElseThrow(() -> new NonExistentLabelException(ExceptionCodeMessage.NON_EXISTENT_LABEL.message()));
    }

}
