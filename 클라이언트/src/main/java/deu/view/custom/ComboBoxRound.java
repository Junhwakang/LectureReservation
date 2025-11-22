package deu.view.custom;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ⭐ Singleton 패턴과 함께 사용되는 둥근 모서리를 가진 ComboBox
 * TextFieldRound와 동일한 스타일 적용
 */
public class ComboBoxRound<E> extends JComboBox<E> {
    
    private int round = 15;
    
    public ComboBoxRound() {
        super();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        
        // ComboBox 렌더러 설정
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    label.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
                }
                return c;
            }
        });
    }
    
    public void setRound(int round) {
        this.round = round;
        repaint();
    }
    
    public int getRound() {
        return round;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape roundShape = new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, round, round);
        g2.setClip(roundShape);
        
        Color bg = getBackground();
        if (bg != null && bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fill(roundShape);
        }
        
        super.paintComponent(g2);
        g2.dispose();
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0));
        Shape border = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, round, round);
        g2.draw(border);
        g2.dispose();
    }
    
    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), round, round);
        return shape.contains(x, y);
    }
}

