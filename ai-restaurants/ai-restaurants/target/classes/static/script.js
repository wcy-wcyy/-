// 全局变量
let currentUser = null;
let allDishes = [];
let userPreferences = {
    cuisines: [],
    flavors: [],
    ingredients: [],
    allergies: [],
    diseases: [],
    dietaryRestrictions: []
};

// API 基础URL
const API_BASE = '/api';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// 初始化应用
async function initializeApp() {
    // 加载统计数据
    await loadStatistics();
    
    // 加载菜品数据
    await loadDishes();
    
    // 设置事件监听器
    setupEventListeners();
    
    // 检查本地存储中的用户信息
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        updateUserDisplay();
        loadUserProfile();
    }
}

// 设置事件监听器
function setupEventListeners() {
    // 表单提交事件
    document.getElementById('profile-form').addEventListener('submit', saveProfile);
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('dish-form').addEventListener('submit', addDish);
    
    // 偏好标签输入事件
    setupTagInput('cuisine-input', 'cuisine-tags', userPreferences.cuisines);
    setupTagInput('flavor-input', 'flavor-tags', userPreferences.flavors);
    setupTagInput('allergy-input', 'allergy-tags', userPreferences.allergies);
    
    // 体重身高变化监听
    document.getElementById('weight').addEventListener('input', updateHealthMetrics);
    document.getElementById('height').addEventListener('input', updateHealthMetrics);
    document.getElementById('age').addEventListener('input', updateHealthMetrics);
    document.getElementById('gender').addEventListener('change', updateHealthMetrics);
}

// 显示指定章节
function showSection(sectionId) {
    // 隐藏所有章节
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => {
        section.classList.remove('active');
    });
    
    // 显示指定章节
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
        
        // 根据章节执行特定初始化
        switch(sectionId) {
            case 'dishes':
                displayDishes(allDishes);
                break;
            case 'recommendations':
                // 清空之前的推荐
                document.getElementById('recommendations-grid').innerHTML = '';
                break;
            case 'nutrition':
                // 清空之前的报告
                document.getElementById('nutrition-report').innerHTML = '';
                break;
        }
    }
}

// 加载统计数据
async function loadStatistics() {
    try {
        const [usersResponse, dishesResponse] = await Promise.all([
            fetch(`${API_BASE}/users`),
            fetch(`${API_BASE}/dishes`)
        ]);
        
        if (usersResponse.ok && dishesResponse.ok) {
            const users = await usersResponse.json();
            const dishes = await dishesResponse.json();
            
            document.getElementById('total-users').textContent = users.length;
            document.getElementById('total-dishes').textContent = dishes.length;
        }
    } catch (error) {
        console.error('加载统计数据失败:', error);
    }
}

// 加载菜品数据
async function loadDishes() {
    try {
        const response = await fetch(`${API_BASE}/dishes`);
        if (response.ok) {
            allDishes = await response.json();
            populateFilters();
        }
    } catch (error) {
        console.error('加载菜品数据失败:', error);
        showToast('加载菜品数据失败', 'error');
    }
}

// 填充筛选器选项
function populateFilters() {
    const cuisines = [...new Set(allDishes.map(dish => dish.cuisine).filter(Boolean))];
    const flavors = [...new Set(allDishes.map(dish => dish.flavor).filter(Boolean))];
    
    const cuisineFilter = document.getElementById('cuisine-filter');
    const flavorFilter = document.getElementById('flavor-filter');
    
    cuisines.forEach(cuisine => {
        const option = document.createElement('option');
        option.value = cuisine;
        option.textContent = cuisine;
        cuisineFilter.appendChild(option);
    });
    
    flavors.forEach(flavor => {
        const option = document.createElement('option');
        option.value = flavor;
        option.textContent = flavor;
        flavorFilter.appendChild(option);
    });
}

// 显示菜品
function displayDishes(dishes) {
    const dishesGrid = document.getElementById('dishes-grid');
    dishesGrid.innerHTML = '';
    
    dishes.forEach(dish => {
        const dishCard = createDishCard(dish);
        dishesGrid.appendChild(dishCard);
    });
}

// 创建菜品卡片
function createDishCard(dish) {
    const card = document.createElement('div');
    card.className = 'dish-card fade-in';
    
    card.innerHTML = `
        <div class="dish-card-header">
            <h3>${dish.name}</h3>
            <p>${dish.description || '美味佳肴'}</p>
        </div>
        <div class="dish-card-body">
            <div class="dish-tags">
                ${dish.cuisine ? `<span class="dish-tag">${dish.cuisine}</span>` : ''}
                ${dish.flavor ? `<span class="dish-tag">${dish.flavor}</span>` : ''}
                ${dish.price ? `<span class="dish-tag">¥${dish.price}</span>` : ''}
            </div>
            <div class="nutrition-info">
                <div class="nutrition-item">
                    <small>热量</small>
                    <span>${dish.calories || '--'} kcal</span>
                </div>
                <div class="nutrition-item">
                    <small>蛋白质</small>
                    <span>${dish.protein || '--'} g</span>
                </div>
                <div class="nutrition-item">
                    <small>脂肪</small>
                    <span>${dish.fat || '--'} g</span>
                </div>
                <div class="nutrition-item">
                    <small>碳水</small>
                    <span>${dish.carbohydrate || '--'} g</span>
                </div>
            </div>
        </div>
    `;
    
    return card;
}

// 筛选菜品
function filterDishes() {
    const cuisineFilter = document.getElementById('cuisine-filter').value;
    const flavorFilter = document.getElementById('flavor-filter').value;
    
    let filteredDishes = allDishes;
    
    if (cuisineFilter) {
        filteredDishes = filteredDishes.filter(dish => dish.cuisine === cuisineFilter);
    }
    
    if (flavorFilter) {
        filteredDishes = filteredDishes.filter(dish => dish.flavor === flavorFilter);
    }
    
    displayDishes(filteredDishes);
}

// 显示登录模态框
function showLoginModal() {
    document.getElementById('login-modal').style.display = 'block';
}

// 显示添加菜品模态框
function showAddDishModal() {
    document.getElementById('dish-modal').style.display = 'block';
}

// 关闭模态框
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// 处理登录
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('login-username').value;
    const email = document.getElementById('login-email').value;
    
    try {
        // 首先尝试通过用户名查找用户
        let response = await fetch(`${API_BASE}/users/username/${username}`);
        
        if (response.ok) {
            currentUser = await response.json();
            // 验证邮箱是否匹配
            if (currentUser.email === email) {
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                updateUserDisplay();
                loadUserProfile();
                closeModal('login-modal');
                showToast('登录成功！', 'success');
            } else {
                showToast('用户名或邮箱不匹配', 'error');
            }
        } else {
            showToast('用户不存在', 'error');
        }
    } catch (error) {
        console.error('登录失败:', error);
        showToast('登录失败', 'error');
    }
}

// 显示注册表单
function showRegisterForm() {
    closeModal('login-modal');
    showSection('profile');
    showToast('请填写个人资料完成注册', 'warning');
}

// 更新用户显示
function updateUserDisplay() {
    if (currentUser) {
        document.getElementById('current-user').textContent = currentUser.username;
        document.getElementById('login-btn').textContent = '退出';
        document.getElementById('login-btn').onclick = logout;
    } else {
        document.getElementById('current-user').textContent = '游客';
        document.getElementById('login-btn').textContent = '登录';
        document.getElementById('login-btn').onclick = showLoginModal;
    }
}

// 退出登录
function logout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    updateUserDisplay();
    // 清空表单
    document.getElementById('profile-form').reset();
    clearTags();
    showToast('已退出登录', 'success');
}

// 加载用户资料
function loadUserProfile() {
    if (!currentUser) return;
    
    // 填充基本信息
    document.getElementById('username').value = currentUser.username || '';
    document.getElementById('email').value = currentUser.email || '';
    document.getElementById('age').value = currentUser.age || '';
    document.getElementById('gender').value = currentUser.gender || '';
    document.getElementById('weight').value = currentUser.weight || '';
    document.getElementById('height').value = currentUser.height || '';
    
    // 填充偏好数据
    userPreferences.cuisines = currentUser.preferredCuisines || [];
    userPreferences.flavors = currentUser.preferredFlavors || [];
    userPreferences.allergies = currentUser.allergies || [];
    
    // 更新标签显示
    updateTagsDisplay('cuisine-tags', userPreferences.cuisines);
    updateTagsDisplay('flavor-tags', userPreferences.flavors);
    updateTagsDisplay('allergy-tags', userPreferences.allergies);
    
    // 更新健康指标
    updateHealthMetrics();
}

// 保存用户资料
async function saveProfile(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const userData = {
        username: formData.get('username'),
        email: formData.get('email'),
        age: formData.get('age') ? parseInt(formData.get('age')) : null,
        gender: formData.get('gender'),
        weight: formData.get('weight') ? parseFloat(formData.get('weight')) : null,
        height: formData.get('height') ? parseFloat(formData.get('height')) : null,
        preferredCuisines: userPreferences.cuisines,
        preferredFlavors: userPreferences.flavors,
        allergies: userPreferences.allergies,
        dietaryRestrictions: userPreferences.dietaryRestrictions
    };
    
    try {
        let response;
        if (currentUser && currentUser.id) {
            // 更新现有用户
            response = await fetch(`${API_BASE}/users/${currentUser.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
        } else {
            // 创建新用户
            response = await fetch(`${API_BASE}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
        }
        
        if (response.ok) {
            currentUser = await response.json();
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            updateUserDisplay();
            showToast('资料保存成功！', 'success');
        } else {
            showToast('保存失败，请检查输入信息', 'error');
        }
    } catch (error) {
        console.error('保存资料失败:', error);
        showToast('保存失败', 'error');
    }
}

// 更新健康指标
function updateHealthMetrics() {
    const weight = parseFloat(document.getElementById('weight').value);
    const height = parseFloat(document.getElementById('height').value);
    const age = parseInt(document.getElementById('age').value);
    const gender = document.getElementById('gender').value;
    
    // 计算BMI
    if (weight && height) {
        const bmi = weight / Math.pow(height / 100, 2);
        document.getElementById('bmi-value').textContent = bmi.toFixed(1);
    } else {
        document.getElementById('bmi-value').textContent = '--';
    }
    
    // 计算BMR
    if (weight && height && age && gender) {
        let bmr;
        if (gender === 'M') {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else if (gender === 'F') {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
        
        if (bmr) {
            document.getElementById('bmr-value').textContent = bmr.toFixed(0) + ' kcal';
        }
    } else {
        document.getElementById('bmr-value').textContent = '--';
    }
}

// 设置标签输入
function setupTagInput(inputId, containerId, array) {
    const input = document.getElementById(inputId);
    input.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            const value = input.value.trim();
            if (value && !array.includes(value)) {
                array.push(value);
                updateTagsDisplay(containerId, array);
                input.value = '';
            }
        }
    });
}

// 更新标签显示
function updateTagsDisplay(containerId, array) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    
    array.forEach((item, index) => {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.innerHTML = `
            ${item}
            <span class="remove-tag" onclick="removeTag('${containerId}', ${index})">&times;</span>
        `;
        container.appendChild(tag);
    });
}

// 移除标签
function removeTag(containerId, index) {
    let array;
    switch(containerId) {
        case 'cuisine-tags':
            array = userPreferences.cuisines;
            break;
        case 'flavor-tags':
            array = userPreferences.flavors;
            break;
        case 'allergy-tags':
            array = userPreferences.allergies;
            break;
    }
    
    if (array) {
        array.splice(index, 1);
        updateTagsDisplay(containerId, array);
    }
}

// 清空所有标签
function clearTags() {
    userPreferences.cuisines = [];
    userPreferences.flavors = [];
    userPreferences.allergies = [];
    
    updateTagsDisplay('cuisine-tags', userPreferences.cuisines);
    updateTagsDisplay('flavor-tags', userPreferences.flavors);
    updateTagsDisplay('allergy-tags', userPreferences.allergies);
}

// 添加菜品
async function addDish(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const dishData = {
        name: formData.get('dish-name'),
        cuisine: formData.get('dish-cuisine'),
        flavor: formData.get('dish-flavor'),
        price: formData.get('dish-price') ? parseFloat(formData.get('dish-price')) : null,
        description: formData.get('dish-description'),
        calories: formData.get('dish-calories') ? parseFloat(formData.get('dish-calories')) : null,
        protein: formData.get('dish-protein') ? parseFloat(formData.get('dish-protein')) : null,
        fat: formData.get('dish-fat') ? parseFloat(formData.get('dish-fat')) : null,
        carbohydrate: formData.get('dish-carbohydrate') ? parseFloat(formData.get('dish-carbohydrate')) : null
    };
    
    try {
        const response = await fetch(`${API_BASE}/dishes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dishData)
        });
        
        if (response.ok) {
            const newDish = await response.json();
            allDishes.push(newDish);
            displayDishes(allDishes);
            closeModal('dish-modal');
            document.getElementById('dish-form').reset();
            showToast('菜品添加成功！', 'success');
            
            // 更新统计数据
            loadStatistics();
        } else {
            showToast('添加菜品失败', 'error');
        }
    } catch (error) {
        console.error('添加菜品失败:', error);
        showToast('添加菜品失败', 'error');
    }
}

// 生成推荐
async function generateRecommendations() {
    if (!currentUser) {
        showToast('请先登录', 'warning');
        return;
    }
    
    const mealType = document.getElementById('meal-type').value;
    
    const requestData = {
        userId: currentUser.id,
        mealType: mealType || null,
        count: 6
    };
    
    try {
        const response = await fetch(`${API_BASE}/recommendations/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });
        
        if (response.ok) {
            const recommendations = await response.json();
            displayRecommendations(recommendations);
            showToast('推荐生成成功！', 'success');
        } else {
            showToast('生成推荐失败', 'error');
        }
    } catch (error) {
        console.error('生成推荐失败:', error);
        showToast('生成推荐失败', 'error');
    }
}

// 显示推荐结果
function displayRecommendations(recommendations) {
    const grid = document.getElementById('recommendations-grid');
    grid.innerHTML = '';
    
    recommendations.forEach(rec => {
        const card = createRecommendationCard(rec);
        grid.appendChild(card);
    });
}

// 创建推荐卡片
function createRecommendationCard(recommendation) {
    const card = document.createElement('div');
    card.className = 'recommendation-card fade-in';
    
    const scoreColor = recommendation.score >= 80 ? '#28a745' : 
                      recommendation.score >= 60 ? '#ffc107' : '#dc3545';
    
    card.innerHTML = `
        <div class="recommendation-score" style="background: ${scoreColor}">
            ${recommendation.score.toFixed(0)}分
        </div>
        <h3>${recommendation.dish.name}</h3>
        <div class="dish-tags">
            <span class="dish-tag">${recommendation.dish.cuisine}</span>
            <span class="dish-tag">${recommendation.dish.flavor}</span>
        </div>
        <div class="recommendation-reason">
            <i class="fas fa-lightbulb"></i>
            ${recommendation.reason}
        </div>
        <div class="match-details">
            <div class="match-item">
                <small>菜系匹配</small>
                <span>${recommendation.cuisineMatch ? (recommendation.cuisineMatch * 100).toFixed(0) + '%' : '--'}</span>
            </div>
            <div class="match-item">
                <small>口味匹配</small>
                <span>${recommendation.flavorMatch ? (recommendation.flavorMatch * 100).toFixed(0) + '%' : '--'}</span>
            </div>
            <div class="match-item">
                <small>健康匹配</small>
                <span>${recommendation.healthMatch ? (recommendation.healthMatch * 100).toFixed(0) + '%' : '--'}</span>
            </div>
        </div>
        <div class="nutrition-info">
            <div class="nutrition-item">
                <small>热量</small>
                <span>${recommendation.dish.calories || '--'} kcal</span>
            </div>
            <div class="nutrition-item">
                <small>蛋白质</small>
                <span>${recommendation.dish.protein || '--'} g</span>
            </div>
            <div class="nutrition-item">
                <small>脂肪</small>
                <span>${recommendation.dish.fat || '--'} g</span>
            </div>
            <div class="nutrition-item">
                <small>碳水</small>
                <span>${recommendation.dish.carbohydrate || '--'} g</span>
            </div>
        </div>
    `;
    
    return card;
}

// 生成营养报告
async function generateNutritionReport() {
    if (!currentUser) {
        showToast('请先登录', 'warning');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/nutrition/quick-report/${currentUser.id}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            const report = await response.json();
            displayNutritionReport(report);
            showToast('营养报告生成成功！', 'success');
        } else {
            showToast('生成营养报告失败', 'error');
        }
    } catch (error) {
        console.error('生成营养报告失败:', error);
        showToast('生成营养报告失败', 'error');
    }
}

// 显示营养报告
function displayNutritionReport(report) {
    const container = document.getElementById('nutrition-report');
    
    const healthScoreClass = report.healthScore >= 80 ? 'score-excellent' : 
                            report.healthScore >= 60 ? 'score-good' : 'score-poor';
    
    container.innerHTML = `
        <div class="nutrition-summary">
            <div class="nutrition-card">
                <h4>总热量</h4>
                <div class="nutrition-value">${report.totalCalories?.toFixed(0) || '--'}</div>
                <small>推荐: ${report.recommendedCalories?.toFixed(0) || '--'} kcal</small>
            </div>
            <div class="nutrition-card">
                <h4>蛋白质</h4>
                <div class="nutrition-value">${report.totalProtein?.toFixed(1) || '--'}</div>
                <small>推荐: ${report.recommendedProtein?.toFixed(1) || '--'} g</small>
            </div>
            <div class="nutrition-card">
                <h4>脂肪</h4>
                <div class="nutrition-value">${report.totalFat?.toFixed(1) || '--'}</div>
                <small>推荐: ${report.recommendedFat?.toFixed(1) || '--'} g</small>
            </div>
            <div class="nutrition-card">
                <h4>碳水化合物</h4>
                <div class="nutrition-value">${report.totalCarbohydrate?.toFixed(1) || '--'}</div>
                <small>推荐: ${report.recommendedCarbohydrate?.toFixed(1) || '--'} g</small>
            </div>
        </div>
        
        <div class="health-assessment">
            <div class="health-score">
                <div class="health-score-circle ${healthScoreClass}">
                    ${report.healthScore || 0}
                </div>
                <h3>健康状态: ${report.healthStatus || '未评估'}</h3>
            </div>
            
            ${report.recommendations ? `
                <div class="recommendations-section">
                    <h4><i class="fas fa-lightbulb"></i> 营养建议</h4>
                    <p>${report.recommendations}</p>
                </div>
            ` : ''}
            
            ${report.warnings ? `
                <div class="warnings-section">
                    <h4><i class="fas fa-exclamation-triangle"></i> 健康提醒</h4>
                    <p style="color: #dc3545;">${report.warnings}</p>
                </div>
            ` : ''}
            
            ${report.includedDishes && report.includedDishes.length > 0 ? `
                <div class="included-dishes">
                    <h4><i class="fas fa-utensils"></i> 包含菜品</h4>
                    <div class="dish-list">
                        ${report.includedDishes.map(dish => `<span class="dish-tag">${dish}</span>`).join('')}
                    </div>
                </div>
            ` : ''}
        </div>
    `;
}

// 显示营养历史
async function showNutritionHistory() {
    if (!currentUser) {
        showToast('请先登录', 'warning');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/nutrition/history/${currentUser.id}`);
        
        if (response.ok) {
            const history = await response.json();
            displayNutritionHistory(history);
        } else {
            showToast('获取历史记录失败', 'error');
        }
    } catch (error) {
        console.error('获取营养历史失败:', error);
        showToast('获取历史记录失败', 'error');
    }
}

// 显示营养历史
function displayNutritionHistory(history) {
    const container = document.getElementById('nutrition-report');
    
    if (history.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #666;">暂无历史记录</p>';
        return;
    }
    
    const historyHTML = history.map(report => `
        <div class="history-item" style="background: white; padding: 1rem; border-radius: 8px; margin-bottom: 1rem; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h4>${new Date(report.reportDate).toLocaleDateString()}</h4>
                <span class="health-badge" style="background: ${report.healthScore >= 80 ? '#28a745' : report.healthScore >= 60 ? '#ffc107' : '#dc3545'}; color: white; padding: 0.25rem 0.75rem; border-radius: 12px;">
                    ${report.healthScore}分
                </span>
            </div>
            <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; text-align: center;">
                <div>
                    <small>热量</small>
                    <div style="font-weight: bold; color: #667eea;">${report.totalCalories?.toFixed(0) || '--'} kcal</div>
                </div>
                <div>
                    <small>蛋白质</small>
                    <div style="font-weight: bold; color: #667eea;">${report.totalProtein?.toFixed(1) || '--'} g</div>
                </div>
                <div>
                    <small>脂肪</small>
                    <div style="font-weight: bold; color: #667eea;">${report.totalFat?.toFixed(1) || '--'} g</div>
                </div>
                <div>
                    <small>碳水</small>
                    <div style="font-weight: bold; color: #667eea;">${report.totalCarbohydrate?.toFixed(1) || '--'} g</div>
                </div>
            </div>
        </div>
    `).join('');
    
    container.innerHTML = `
        <h3 style="margin-bottom: 1rem;"><i class="fas fa-history"></i> 营养历史记录</h3>
        ${historyHTML}
    `;
}

// 显示Toast通知
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 点击模态框外部关闭
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
} 