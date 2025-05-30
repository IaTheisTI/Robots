package log;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LogWindowSource {
    private final int m_iQueueLength;
    private final LogEntry[] m_messages;
    private int m_start;
    private int m_size;
    private final ReadWriteLock m_lock = new ReentrantReadWriteLock();
    private final CopyOnWriteArrayList<LogChangeListener> m_listeners = new CopyOnWriteArrayList<>();

    /**
     * Создает новый источник лога с указанным максимальным размером буфера.
     * @param iQueueLength максимальное количество хранимых записей (должно быть положительным)
     */
    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LogEntry[iQueueLength];
        m_start = 0;
        m_size = 0;
    }


    public void registerListener(LogChangeListener listener) {
        if (listener != null) {
            m_listeners.addIfAbsent(listener);
        }
    }


    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    /**
     * Добавляет новую запись в лог.
     * Если буфер заполнен, самая старая запись вытесняется.
     * После добавления уведомляет всех зарегистрированных слушателей.
     * @param logLevel уровень важности записи
     * @param strMessage текст сообщения
     */
    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        m_lock.writeLock().lock();
        try {
            int index = (m_start + m_size) % m_iQueueLength;
            m_messages[index] = entry;

            if (m_size < m_iQueueLength) {
                m_size++;
            } else {
                m_start = (m_start + 1) % m_iQueueLength;
            }
        } finally {
            m_lock.writeLock().unlock();
        }

        notifyListeners();
    }


    private void notifyListeners() {
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }


    /**
     * Возвращает текущее количество записей в логе.
     * @return число записей (0 <= size <= m_iQueueLength)
     */
    public int size() {
        m_lock.readLock().lock();
        try {
            return m_size;
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Возвращает диапазон записей из лога.
     * @param startFrom начальный индекс (0-based)
     * @param count максимальное количество записей для возврата
     * @return неизменяемый список записей (пустой список, если startFrom некорректен)
     * @throws IndexOutOfBoundsException если startFrom < 0
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        m_lock.readLock().lock();
        try {
            if (startFrom < 0 || startFrom >= m_size) {
                return Collections.emptyList();
            }

            int end = Math.min(startFrom + count, m_size);
            List<LogEntry> result = new ArrayList<>(end - startFrom);

            for (int i = startFrom; i < end; i++) {
                int index = (m_start + i) % m_iQueueLength;
                result.add(m_messages[index]);
            }

            return Collections.unmodifiableList(result);
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Возвращает все записи лога.
     * Эквивалентно вызову range(0, size()).
     * @return неизменяемый список всех записей
     */
    public Iterable<LogEntry> all() {
        return range(0, m_size);
    }
}