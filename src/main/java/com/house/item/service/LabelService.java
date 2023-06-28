package com.house.item.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateLabel;
import com.house.item.domain.LabelRS;
import com.house.item.domain.UpdateLabelRQ;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.repository.LabelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelService {

    private final LabelRepository labelRepository;
    private final AuthService authService;

    @Transactional
    public Long createLabel(CreateLabel createLabel, User user) throws NonUniqueLabelNameException {
        validateDuplicationLabelName(createLabel.getName(), user);

        Label label = Label.builder()
            .user(user)
            .name(createLabel.getName())
            .build();

        labelRepository.save(label);
        return label.getLabelNo();
    }

    private void validateDuplicationLabelName(String name, User user) throws NonUniqueLabelNameException {
        List<Label> labels = labelRepository.findByNameAndUser(name, user);
        if (!labels.isEmpty()) {
            throw new NonUniqueLabelNameException(ExceptionCodeMessage.NON_UNIQUE_LABEL_NAME.message());
        }
    }

    public Label getLabel(Long labelNo, User user) throws NonExistentLabelException {
        Label label = labelRepository.findById(labelNo)
            .orElseThrow(() -> new NonExistentLabelException(ExceptionCodeMessage.NON_EXISTENT_LABEL.message()));

        if (label.getUser().getUserNo().equals(user.getUserNo())) {
            return label;
        }
        throw new NonExistentLabelException(ExceptionCodeMessage.NON_EXISTENT_LABEL.message());
    }

    public List<Label> getLabels(User user) {
        return labelRepository.findByUser(user);
    }

    public List<LabelRS> labelToLabelRS(List<Label> labels) {
        List<LabelRS> labelRSList = new ArrayList<>();
        for (Label label : labels) {
            labelRSList.add(LabelRS.builder()
                .labelNo(label.getLabelNo())
                .name(label.getName())
                .build());
        }
        return labelRSList;
    }

    @Transactional
    public void deleteLabel(Long labelNo, User user) throws NonExistentLabelException {
        Label label = getLabel(labelNo, user);
        labelRepository.delete(label);
    }

    @Transactional
    public void updateLabel(Long labelNo, UpdateLabelRQ updateLabelRQ, User user) throws
        NonExistentLabelException,
        NonUniqueLabelNameException {
        validateDuplicationLabelName(updateLabelRQ.getName(), user);
        Label label = getLabel(labelNo, user);
        label.updateLabel(updateLabelRQ.getName());
    }
}
