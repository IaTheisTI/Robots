package log;

import java.util.*;

/**
 * Класс источник данных для окна логирования
 */
public class LogWindowSource
{
    /**
     * количество сообщений в логе
     */
    private int m_iQueueLength;

    /**
     * сиписок сообщений лога
     */
    private LinkedList<LogEntry> m_messages;

    /**
     * список слушателей этого класса
     */
    private final ArrayList<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength)
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }

    /**
     * добавление слушателя
     * @param listener - слушатель
     */
    public synchronized void registerListener(LogChangeListener listener)
    {
        m_listeners.add(listener);
    }

    /**
     * удаление слушателей
     * @param listener - слушатель
     */
    public synchronized void unregisterListener(LogChangeListener listener)
    {
        m_listeners.remove(listener);
    }

    /**
     * добавление сообщения в лог
     * @param logLevel - уровень логирования
     * @param strMessage - текст сообщения
     */
    public synchronized void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        if(m_messages.size() == m_iQueueLength){
            m_messages.removeFirst();
        }
        m_messages.add(entry);
        for (LogChangeListener listener : m_listeners)
        {
            listener.onLogChanged();
        }
    }

    /**
     * получение сообщений из заданного диапазона
     * @param startFrom - индекс начала диапазона
     * @param count - количество сообщений в диапазоне
     * @return - итерируемый объект - набор сообщений из диапазона
     */
    public synchronized Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= m_messages.size())
        {
            return null;
        }
        int indexTo = Math.min(startFrom + count, m_messages.size());
        return m_messages.subList(startFrom, indexTo);
    }

    /**
     * получение итерируемого объекта от списка сообщений
     * @return - итерируемый объект из всех сообщений лога
     */
    public synchronized Iterable<LogEntry> all()
    {
        return new ArrayList<>(m_messages);
    }
}