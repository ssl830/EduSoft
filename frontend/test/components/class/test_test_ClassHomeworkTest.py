pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import Page, expect

BASE_URL = "http://localhost:3000/class/2"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

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
            pytest.fail(f"用户 {credentials['userId']} 无法访问 {class_url}，测试失败")
        expect(page).to_have_url(re.compile(r"http://localhost:3000/class/2"))
    return browser, context

def goto_homework_tab(page: Page):
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/class/2"))
    expect(page.locator(".course-header h1")).to_be_visible(timeout=10000)
    page.locator("button:has-text('课程作业')").click()
    expect(page.locator(".homework-container")).to_be_visible()

# def test_teacher_homework_flow(playwright):
#     browser, context = login_and_get_context(playwright, TEACHER_CREDENTIALS, BASE_URL)
#     page = context.new_page()
#     goto_homework_tab(page)
#     expect(page.locator(".create-btn")).to_be_visible()
#     page.locator(".create-btn").click()
#     expect(page.locator(".modal-header h3")).to_have_text("创建作业")
#     page.locator("input[placeholder='输入作业名称']").fill("自动化测试作业")
#     page.locator("textarea[placeholder='详细描述作业要求']").fill("测试描述")
#     page.locator("input[type='datetime-local']").fill("2099-12-31T23:59")
#     page.locator(".btn-submit").click()
#     expect(page.locator(".modal-backdrop")).not_to_be_visible(timeout=5000)
#     expect(page.locator(".homework-table")).to_contain_text("自动化测试作业")
#     page.locator(".btn-view").first.click()
#     expect(page.locator(".submissions-modal")).to_be_visible()
#     page.locator(".close-btn").click()
#     expect(page.locator(".submissions-modal")).not_to_be_visible(timeout=3000)
#     page.locator(".btn-delete").first.click()
#     expect(page.locator(".confirm-modal")).to_be_visible()
#     page.locator(".confirm-modal .btn-delete").click()
#     expect(page.locator(".confirm-modal")).not_to_be_visible(timeout=3000)
#     context.close()
#     browser.close()

def test_student_homework_view(playwright):
    browser, context = login_and_get_context(playwright, STUDENT_CREDENTIALS, BASE_URL)
    page = context.new_page()
    goto_homework_tab(page)
    expect(page.locator(".create-btn")).not_to_be_visible()
    expect(page.locator(".homework-list")).to_be_visible()
    if page.locator(".homework-table").count() > 0 and page.locator(".homework-table").is_visible():
        expect(page.locator(".homework-table")).to_be_visible()
        page.locator(".btn-view").first.click()
        expect(page.locator(".detail-modal")).to_be_visible()
        expect(page.locator(".description")).to_be_visible()
        page.locator(".close-btn").click()
        expect(page.locator(".detail-modal")).not_to_be_visible(timeout=3000)
    else:
        assert page.locator(".homework-table").count() == 0
    context.close()
    browser.close()

def test_admin_login(playwright):
    browser, context = login_and_get_context(playwright, ADMIN_CREDENTIALS)
    page = context.new_page()
    page.goto("http://localhost:3000/")
    expect(page.locator("body")).to_be_visible()
    context.close()
    browser.close()
