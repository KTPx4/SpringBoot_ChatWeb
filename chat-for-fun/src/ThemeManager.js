const themes = {
    theme_light: {
        key: 'theme_light',
        background: '#ffffff',
        textColor: '#000000',
        slider: '#ffffff',
        card: '#ffffff',
        cardSelected: "rgb(248,216,251)",
        content: 'linear-gradient(rgba(254, 238, 255), rgb(255, 255, 255))',
        navContent: '#ffffff',
    },
    theme_dark: {
        key: 'theme_dark',
        background: '#27374D',
        textColor: '#ffffff',
        slider: '#001529',
        card: '#293948',
        cardSelected: '#144272',
        content: 'linear-gradient(rgb(2 18 33),  #4C516D)',
        navContent: '#001529',
    },

};

// Hàm lấy theme từ localStorage hoặc mặc định là 'theme_light'
const getCurrentTheme = () => {
    const savedTheme = localStorage.getItem('app_theme');
    return themes[savedTheme] || themes.theme_light;
};

// Đối tượng theme có các phương thức lấy mã màu dựa trên theme hiện tại
const themeManager = {
    getKey: () => getCurrentTheme().key,
    getBackground: () => getCurrentTheme().background,
    getSlider: () => getCurrentTheme().slider,
    getCard: () => getCurrentTheme().card,
    getCardSelected: () => getCurrentTheme().cardSelected,
    getContent: () => getCurrentTheme().content,
    getText: () => getCurrentTheme().textColor,
    getNavContent: () => getCurrentTheme().navContent,
    setTheme: (newTheme) => {
        if (themes[newTheme]) {
            localStorage.setItem('app_theme', newTheme);
        }
    },
};

export default themeManager;