pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/dashboard"
LOGIN_URL = "http://localhost:3000/login"
TUTOR_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if goto_url:
        if page.url.startswith("http://localhost:3000/login?redirect="):
            page.goto(goto_url)
        else:
            page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_dashboard_overview_render(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".dashboard-overview")).to_be_visible(timeout=10000)
    expect(page.locator(".header-bar h2")).to_have_text("系统概览")
    # 日视图卡片
    expect(page.locator(".stats-section").first).to_be_visible()
    # 图表模式切换
    page.locator(".display-toggle button:has-text('可视化图表')").click()
    expect(page.locator(".charts-grid")).to_be_visible()
    context.close()
    browser.close()

def test_dashboard_overview_switch_modes(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".dashboard-overview")).to_be_visible(timeout=10000)
    # 切换到周视图
    page.locator(".view-toggle button:has-text('周视图')").click()
    expect(page.locator(".stats-section h3").first).to_have_text("近7日概览")
    # 切换回日视图
    page.locator(".view-toggle button:has-text('日视图')").click()
    expect(page.locator(".stats-section h3").first).to_have_text("今日概览")
    context.close()
    browser.close()

def test_dashboard_overview_optimize_btn(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".dashboard-overview")).to_be_visible(timeout=10000)
    # 展开日视图课程章节标签，点击AI优化建议按钮（如有）
    optimize_btn = page.locator(".course-section-tags .q-btn[icon='tune']").first
    if optimize_btn.count() > 0 and optimize_btn.is_visible():
        optimize_btn.click()
        # 检查弹窗出现
        expect(page.locator(".q-dialog")).to_be_visible(timeout=5000)
    context.close()
    browser.close()

