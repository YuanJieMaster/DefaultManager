package com.xquant.defaultmanager.config;

import com.xquant.defaultmanager.entity.BreachReason;
import com.xquant.defaultmanager.entity.RebirthReason;
import com.xquant.defaultmanager.repository.BreachReasonRepository;
import com.xquant.defaultmanager.repository.RebirthReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final BreachReasonRepository breachReasonRepository;
    private final RebirthReasonRepository rebirthReasonRepository;

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否已有违约原因数据，如果没有则初始化
        if (breachReasonRepository.count() == 0) {
            initBreachReasons();
        }
        
        // 检查是否已有重生原因数据，如果没有则初始化
        if (rebirthReasonRepository.count() == 0) {
            initRebirthReasons();
        }
    }

    private void initBreachReasons() {
        List<BreachReason> reasons = Arrays.asList(
                createBreachReason(1, "6个月内，交易对手技术性或资金等原因，给当天结算带来头寸缺口2次以上", true),
                createBreachReason(2, "6个月内因各种原因导致成交后撤单2次以上", true),
                createBreachReason(3, "未能按照合约规定支付或延期支付利息，本金或其他交付义务（不包括在宽限期内延期支付）", true),
                createBreachReason(4, "关联违约：如果集团（内部联系较紧密的集团）或集团内任一公司（较重要的子公司，一旦发生违约会对整个集团造成较大影响的）发生违约，可视情况作为集团内所有成员违约的触发条件", true),
                createBreachReason(5, "发生消极债务置换：债务人提供给债权人新的或重组的债务，或新的证券组合、现金或资产低于原有金融义务；或为了债务人未来避免发生破产或拖欠还款而进行的展期或重组", true),
                createBreachReason(6, "申请破产保护，发生法律接管，或者处于类似的破产保护状态", true),
                createBreachReason(7, "在其他金融机构违约（包括不限于：人行征信记录中显示贷款分类状态不良类情况，逾期超过90天等），或外部评级显示为违约级别", true)
        );
        
        breachReasonRepository.saveAll(reasons);
    }

    private void initRebirthReasons() {
        List<RebirthReason> reasons = Arrays.asList(
                createRebirthReason(1, "正常结算后解除", true),
                createRebirthReason(2, "在其他金融机构违约解除，或外部评级显示为非违约级别", true),
                createRebirthReason(3, "计提比例小于设置界限", true),
                createRebirthReason(4, "连续12个月内按时支付本金和利息", true),
                createRebirthReason(5, "客户的还款意愿和还款能力明显好转，已偿付各项逾期本金、逾期利息和其他费用（包括罚息等），且连续12个月内按时支付本金、利息", true),
                createRebirthReason(6, "导致违约的关联集团内其他发生违约的客户已经违约重生，解除关联成员的违约设定", true)
        );
        
        rebirthReasonRepository.saveAll(reasons);
    }

    private BreachReason createBreachReason(Integer sortOrder, String reasonContent, Boolean isEnabled) {
        BreachReason reason = new BreachReason();
        reason.setSortOrder(sortOrder);
        reason.setReasonContent(reasonContent);
        reason.setIsEnabled(isEnabled);
        return reason;
    }
    
    private RebirthReason createRebirthReason(Integer sortOrder, String reasonContent, Boolean isEnabled) {
        RebirthReason reason = new RebirthReason();
        reason.setSortOrder(sortOrder);
        reason.setReasonContent(reasonContent);
        reason.setIsEnabled(isEnabled);
        return reason;
    }
}