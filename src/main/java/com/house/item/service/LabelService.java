package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateLabel;
import com.house.item.domain.LabelRS;
import com.house.item.domain.SessionUser;
import com.house.item.domain.UpdateLabelRQ;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.repository.LabelRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Long createLabel(CreateLabel createLabel) throws NonUniqueLabelNameException {
        User loginUser = authService.getLoginUser();
        validateDuplicationLabelName(createLabel.getName());

        Label label = Label.builder()
                .user(loginUser)
                .name(createLabel.getName())
                .build();

        labelRepository.save(label);
        return label.getLabelNo();
    }

    private void validateDuplicationLabelName(String name) throws NonUniqueLabelNameException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        labelRepository.findByNameAndUserNo(name, sessionUser.getUserNo())
                .ifPresent(l -> {
                    throw new NonUniqueLabelNameException(ExceptionCodeMessage.NON_UNIQUE_LABEL_NAME.message());
                });
    }

    public Label getLabel(Long labelNo) throws NonExistentLabelException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return labelRepository.findByLabelNoAndUserNo(labelNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentLabelException(ExceptionCodeMessage.NON_EXISTENT_LABEL.message()));
    }

    public List<Label> getLabels() {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return labelRepository.findByUserNo(sessionUser.getUserNo());
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
    public void deleteLabel(Long labelNo) throws NonExistentLabelException {
        getLabel(labelNo);
        labelRepository.deleteByLabelNo(labelNo);
    }

    @Transactional
    public void updateLabel(Long labelNo, UpdateLabelRQ updateLabelRQ) throws NonExistentLabelException, NonUniqueLabelNameException {
        validateDuplicationLabelName(updateLabelRQ.getName());
        Label label = getLabel(labelNo);
        label.updateLabel(updateLabelRQ.getName());
    }
}
