package org.destiny.activiti.interceptor;

import org.activiti.engine.debug.ExecutionTreeUtil;
import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.interceptor.DebugCommandInvoker;
import org.activiti.engine.logging.LogMDC;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/2 00:39
 * @version 1.8
 * @since JDK 1.8.0_101
 */
public class MDCCommandInvoker extends DebugCommandInvoker {

    /**
     * 先判断可运行的对象是不是 Activiti 支持的 Operation
     * 如果是, 将它强转, 并取出执行对象并输出
     *
     * @param runnable
     */
    @Override
    public void executeOperation(Runnable runnable) {
        boolean mdcEnabled = LogMDC.isMDCEnabled();
        LogMDC.setMDCEnabled(true);
        if (runnable instanceof AbstractOperation) {
            AbstractOperation operation = (AbstractOperation) runnable;
            if (operation.getExecution() != null) {
                // 如果是可操作对象, 将该信息放入 MDC 上下文对象
                LogMDC.putMDCExecution(operation.getExecution());
            }
        }

        super.executeOperation(runnable);
        LogMDC.clear();
        if (!mdcEnabled) {
            // 如果 MDC 原本不生效, 需要将 MDC 重新置为 false
            LogMDC.setMDCEnabled(false);
        }
    }
}
