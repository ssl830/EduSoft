pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/manage/tutors"
LOGIN_URL = "http://localhost:3000/login"
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

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
    expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/tutors"))
    return browser, context, page

def test_tutors_list_render(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS, BASE_URL)
    expect(page.locator("h2")).to_have_text("管理员基本信息列表")
    expect(page.locator(".tutors-table")).to_be_visible(timeout=10000)
    # 检查表头字段
    headers = ["ID", "用户ID", "姓名", "邮箱", "创建时间", "操作"]
    for h in headers:
        expect(page.locator(f".tutors-table th:text-is('{h}')")).to_be_visible()
    # 检查至少有一行数据
    row_count = page.locator(".tutors-table tbody tr").count()
    assert row_count > 0
    # 检查删除按钮可见
    expect(page.locator(".btn-action.danger").first).to_be_visible()
    context.close()
    browser.close()

def test_tutors_list_delete_modal(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS, BASE_URL)
    # 找到第一��可用的删除按钮（不是自己的账号）
    btns = page.locator(".btn-action.danger")
    found = False
    for i in range(btns.count()):
        btn = btns.nth(i)
        if btn.is_enabled():
            btn.click()
            found = True
            break
    assert found, "没有可用的删除按钮"
    # 检查弹窗出现
    expect(page.locator(".modal-dialog")).to_be_visible(timeout=3000)
    expect(page.locator(".modal-title")).to_have_text("确认删除")
    expect(page.locator(".modal-content")).to_have_text("确定要删除该管理员吗？")
    # 取消按钮关闭弹窗
    page.locator(".modal-actions .btn-action").last.click()
    expect(page.locator(".modal-dialog")).not_to_be_visible(timeout=3000)
    context.close()
    browser.close()

def test_tutors_list_self_delete_disabled(playwright):
    browser, context, page = login_and_get_page(playwright, ADMIN_CREDENTIALS, BASE_URL)
    # 检查自己的删除按钮不可用
    rows = page.locator(".tutors-table tbody tr")
    for i in range(rows.count()):
        row = rows.nth(i)
        user_id = row.locator("td").nth(1).inner_text()
        if user_id == ADMIN_CREDENTIALS["userId"]:
            btn = row.locator(".btn-action.danger")
            assert not btn.is_enabled()
    context.close()
    browser.close()
