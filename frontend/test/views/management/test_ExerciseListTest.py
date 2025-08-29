pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/manage/exercise"
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

def test_exercise_list_render(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-list-page")).to_be_visible(timeout=10000)
    expect(page.locator(".filters-bar")).to_be_visible()
    # 只在选择完教师和班级后断言练习表格是否可见，否则断言空提示
    if page.locator("select").first.count() > 0 and page.locator("select").nth(1).count() > 0:
        # 选择第一个教师和班级
        page.locator("select").first.select_option(index=1)
        page.locator("select").nth(1).select_option(index=1)
        # 等待练习表格渲染
        expect(page.locator(".exercise-table")).to_be_visible(timeout=5000)
    else:
        # 没有选择教师和班级时，断言空提示可见
        expect(page.locator(".empty-hint")).to_be_visible()
    context.close()
    browser.close()

def test_exercise_list_filter_teacher_class(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-list-page")).to_be_visible(timeout=10000)
    teacher_select = page.locator("select").first
    teacher_select.select_option(index=1)
    class_select = page.locator("select").nth(1)
    class_select.select_option(index=1)
    expect(page.locator(".exercise-table")).to_be_visible()
    context.close()
    browser.close()

def test_exercise_list_view_edit_delete(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".exercise-list-page")).to_be_visible(timeout=10000)
    # 查看练习
    view_btn = page.locator(".btn-action.download").first
    if view_btn.count() > 0 and view_btn.is_visible():
        view_btn.click()
        expect(page).to_have_url(re.compile(r"/exercise/edit/\d+"))
        page.go_back()
    # 编辑练习
    edit_btn = page.locator(".btn-action.preview").first
    if edit_btn.count() > 0 and edit_btn.is_visible():
        edit_btn.click()
        expect(page).to_have_url(re.compile(r"/exercise/edit/\d+"))
        page.go_back()
    # 删除练习
    delete_btn = page.locator(".btn-action.delete").first
    if delete_btn.count() > 0 and delete_btn.is_visible():
        delete_btn.click()
        expect(page.locator(".modal-dialog")).to_be_visible()
        page.locator(".modal-btn.confirm").click()
        # 删除后弹窗消失
        expect(page.locator(".modal-dialog")).not_to_be_visible(timeout=5000)
    context.close()
    browser.close()
