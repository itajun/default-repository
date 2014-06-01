package br.ivj.sandbox.batch.listeners;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component("loggerSandboxStepListener")
public class LoggerSandboxStepListener<T, S> extends StepListenerSupport<T, S> {
	private Logger logger = LoggerFactory.getLogger(LoggerSandboxStepListener.class);

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.debug(String.format("afterStep [%s]", stepExecution.getStepName()));
		return super.afterStep(stepExecution);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug(String.format("beforeStep [%s]", stepExecution.getStepName()));
		super.beforeStep(stepExecution);
	}

	@Override
	public void afterChunk(ChunkContext context) {
		logger.debug(String.format("afterChunk [%s]", context.getStepContext().getStepName()));
		super.afterChunk(context);
	}

	@Override
	public void beforeChunk(ChunkContext context) {
		logger.debug(String.format("beforeChunk [%s]", context.getStepContext().getStepName()));
		super.beforeChunk(context);
	}

	@Override
	public void afterRead(T item) {
		logger.debug(String.format("afterRead [%s]", item));
		super.afterRead(item);
	}

	@Override
	public void beforeRead() {
		logger.debug(String.format("beforeRead"));
		super.beforeRead();
	}

	@Override
	public void onReadError(Exception ex) {
		logger.error(String.format("onReadError [%s]", ex.getMessage()), ex);
		super.onReadError(ex);
	}

	@Override
	public void afterWrite(List<? extends S> items) {
		logger.debug(String.format("afterWrite [%s]", items));
		super.afterWrite(items);
	}

	@Override
	public void beforeWrite(List<? extends S> items) {
		logger.debug(String.format("beforeWrite [%s]", items));
		super.beforeWrite(items);
	}

	@Override
	public void onWriteError(Exception exception, List<? extends S> items) {
		logger.error(String.format("onWriteError [%s]", exception.getMessage()), exception);
		logger.debug(String.format("onWriteError items [%s]", items));
		super.onWriteError(exception, items);
	}

	@Override
	public void afterProcess(T item, S result) {
		logger.debug(String.format("afterProcess [%s] [%s]", item, result));
		super.afterProcess(item, result);
	}

	@Override
	public void beforeProcess(T item) {
		logger.debug(String.format("beforeProcess [%s]", item));
		super.beforeProcess(item);
	}

	@Override
	public void onProcessError(T item, Exception e) {
		logger.error(String.format("onProcessError [%s]", e.getMessage()), e);
		logger.debug(String.format("onProcessError item [%s]", item));
		super.onProcessError(item, e);
	}

	@Override
	public void onSkipInProcess(T item, Throwable t) {
		logger.warn(String.format("onSkipInProcess [%s]", t.getMessage()), t);
		logger.debug(String.format("onSkipInProcess item [%s]", item));
		super.onSkipInProcess(item, t);
	}

	@Override
	public void onSkipInRead(Throwable t) {
		logger.warn(String.format("onSkipInRead [%s]", t.getMessage()), t);
		super.onSkipInRead(t);
	}

	@Override
	public void onSkipInWrite(S item, Throwable t) {
		logger.warn(String.format("onSkipInWrite [%s]", t.getMessage()), t);
		logger.debug(String.format("onSkipInWrite item [%s]", item));
		super.onSkipInWrite(item, t);
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		logger.error(String.format("afterChunkError [%s]", context.getStepContext().getStepName()));
		super.afterChunkError(context);
	}
	
	
}
