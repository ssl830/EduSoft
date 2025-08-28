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

def goto_student_tab(page: Page):
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/class/2"))
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)
    page.locator("button:has-text('班级成员管理')").click()
    expect(page.locator(".resource-list-container")).to_be_visible()

def test_teacher_student_manage(playwright):
    browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_student_tab(page)
    # 检查批量导入和手动导入按钮
    expect(page.locator("button:has-text('批量导入')")).to_be_visible()
    expect(page.locator("button:has-text('手动导入')")).to_be_visible()
    # 检查学生列表
    expect(page.locator(".resource-table")).to_be_visible()
    # 检查删除按钮（如果有学生且不是老师自己）
    if page.locator(".btn-action.preview").count() > 0:
        page.locator(".btn-action.preview").first.click()
        # 弹窗确认
        if page.locator(".dialog-overlay").is_visible():
            page.locator(".dialog-overlay .btn-primary").click()
    context.close()
    browser.close()

def test_student_student_list_readonly(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_student_tab(page)
    # 学生视角下无导入按钮
    expect(page.locator("button:has-text('批量导入')")).not_to_be_visible()
    expect(page.locator("button:has-text('手动导入')")).not_to_be_visible()
    # 学生列表只读
    expect(page.locator(".resource-table")).to_be_visible()
    context.close()
    browser.close()

