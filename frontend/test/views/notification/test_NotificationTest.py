pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/notifications"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, target_url: str):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    # 登录后不强制断言跳转到 target_url
    return browser, context, page

def test_teacher_notification_center(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    # 登录后教师会被重定向到 /manage/teachers，需要手动跳转到通知中心
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/notifications"))
    expect(page.locator("h1.title")).to_have_text(re.compile("通知中心"))
    expect(page.locator(".filter-tabs")).to_be_visible()
    expect(page.locator(".notification-list")).to_be_visible(timeout=10000)
    expect(page.locator(".create-notification-btn")).to_be_visible()
    expect(page.locator(".action-btn:has-text('全部已读')")).to_be_visible()
    expect(page.locator(".action-btn:has-text('设置')")).to_be_visible()
    expect(page.locator(".section-title")).to_contain_text("我的任务清单")
    context.close()
    browser.close()

def test_student_notification_center(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    # 学生登录后可直接访问通知中心
    page.goto(BASE_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/notifications"))
    expect(page.locator("h1.title")).to_have_text(re.compile("通知中心"))
    expect(page.locator(".filter-tabs")).to_be_visible()
    expect(page.locator(".notification-list")).to_be_visible(timeout=10000)
    expect(page.locator(".create-notification-btn")).not_to_be_visible()
    expect(page.locator(".action-btn:has-text('全部已读')")).to_be_visible()
    expect(page.locator(".action-btn:has-text('设置')")).to_be_visible()
    expect(page.locator(".section-title")).to_contain_text("我的任务清单")
    context.close()
    browser.close()