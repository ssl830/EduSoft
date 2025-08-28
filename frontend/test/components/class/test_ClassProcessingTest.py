pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import Page, expect

BASE_URL = "http://localhost:3000/class/2"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_context(playwright, credentials: dict, class_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if class_url:
        page.goto(class_url)
        if page.url.startswith(LOGIN_URL):
            context.close()
            browser.close()
            pytest.skip(f"用户 {credentials['userId']} 无法访问 {class_url}，跳过相关测试")
        expect(page).to_have_url(re.compile(r"http://localhost:3000/class/2"))
    return browser, context

def goto_processing_tab(page: Page):
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/class/2"))
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)
    page.locator("button:has-text('学习进度')").click()
    expect(page.locator(".resource-list-container")).to_be_visible()

def test_teacher_processing_list_and_corrections(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_processing_tab(page)
    expect(page.locator(".resource-header h2")).to_have_text("练习管理")
    expect(page.locator("button:has-text('新建练习')")).to_be_visible()
    page.locator("button:has-text('待批改')").click()
    expect(page.locator(".resource-table")).to_be_visible(timeout=5000)
    # 选择“Tensor Flow 练习1”
    select_locator = page.locator("select#typeFilter")
    select_locator.select_option(label="Tensor Flow 练习1")
    # 等待筛选结果刷新
    page.wait_for_timeout(1000)
    # 如果有批改按钮，点击第一个
    if page.locator(".btn-action.history").count() > 0:
        with page.expect_navigation(url=re.compile(r"/checkExercise/")):
            page.locator(".btn-action.history").first.click()
        expect(page).to_have_url(re.compile(r"/checkExercise/"))
    context.close()
    browser.close()

def test_student_processing_list(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_processing_tab(page)
    expect(page.locator(".resource-header h2")).to_have_text("我的练习")
    expect(page.locator(".resource-table")).to_be_visible(timeout=5000)
    # 如果有练习按钮，且按钮未禁用，点击第一个
    preview_btns = page.locator(".btn-action.preview")
    if preview_btns.count() > 0:
        first_btn = preview_btns.first
        if first_btn.is_enabled():
            with page.expect_navigation(url=re.compile(r"/takeExercise/")):
                first_btn.click()
            expect(page).to_have_url(re.compile(r"/takeExercise/"))
        else:
            # 按钮不可用，跳过点击和断言
            pass
    context.close()
    browser.close()
