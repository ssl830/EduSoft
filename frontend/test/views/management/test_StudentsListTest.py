pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/manage/students"
LOGIN_URL = "http://localhost:3000/login"
TUTOR_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, target_url: str):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(target_url)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/students"))
    return browser, context, page

def test_students_list_render(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator("h2")).to_have_text("学生基本信息列表")
    expect(page.locator(".students-table")).to_be_visible(timeout=10000)
    # 检查表头字段
    headers = ["ID", "用户ID", "姓名", "邮箱", "创建时间", "操作"]
    for h in headers:
        expect(page.locator(f".students-table th:text-is('{h}')")).to_be_visible()
    # 检查至少有一行数据
    row_count = page.locator(".students-table tbody tr").count()
    assert row_count > 0
    # 检查删除按钮可见
    expect(page.locator(".btn-action.danger").first).to_be_visible()
    context.close()
    browser.close()

def test_students_list_delete_modal(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    # 点击第一个删除按钮
    btn = page.locator(".btn-action.danger").first
    btn.click()
    # 检查弹窗出现
    expect(page.locator(".modal-dialog")).to_be_visible(timeout=3000)
    expect(page.locator(".modal-title")).to_have_text("确认删除")
    expect(page.locator(".modal-content")).to_have_text("确定要删除该学生吗？")
    # 取消按钮关闭弹窗
    page.locator(".modal-actions .btn-action").last.click()
    expect(page.locator(".modal-dialog")).not_to_be_visible(timeout=3000)
    context.close()
    browser.close()
