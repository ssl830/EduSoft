pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/exercise/edit/5"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}

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

def test_exercise_edit_page_render(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-edit-layout")).to_be_visible(timeout=10000)
    expect(page.locator("h2.main-title")).to_have_text("编辑练习")
    expect(page.locator("input[type='text']")).to_be_visible()
    expect(page.locator("input[type='datetime-local']").first).to_be_visible()
    expect(page.locator("input[type='datetime-local']").nth(1)).to_be_visible()
    # 修正断言，分别断言两个表格可见
    tables = page.locator(".q-table")
    assert tables.count() == 2
    expect(tables.nth(0)).to_be_visible()
    expect(tables.nth(1)).to_be_visible()
    context.close()
    browser.close()

def test_exercise_edit_update_score(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-edit-layout")).to_be_visible(timeout=10000)
    score_input = page.locator(".score-input").first
    score_input.fill("10")
    expect(score_input).to_have_value("10")
    context.close()
    browser.close()

def test_exercise_edit_add_question_from_bank(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-edit-layout")).to_be_visible(timeout=10000)
    # 右侧题库勾选第一个题目
    bank_checkbox = page.locator(".exercise-edit-bank input[type='checkbox']").first
    bank_checkbox.check()
    # 点击添加全部题目按钮
    add_btn = page.locator(".exercise-edit-bank .btn.btn-primary")
    add_btn.click()
    # 检查题目已添加到左侧列表（断言至少有一个题目行可见）
    rows = page.locator(".exercise-edit-main .q-table tr")
    assert rows.count() > 1  # 包含表头，至少有一个题目
    expect(rows.nth(1)).to_be_visible()
    context.close()
    browser.close()
